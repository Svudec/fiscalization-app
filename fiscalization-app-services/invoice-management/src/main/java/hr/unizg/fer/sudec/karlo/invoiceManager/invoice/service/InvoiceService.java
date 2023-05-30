package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service;

import hr.unizg.fer.sudec.karlo.amqp.RabbitMqMessageProducer;
import hr.unizg.fer.sudec.karlo.amqp.config.FiscalizationQueuesConfig;
import hr.unizg.fer.sudec.karlo.invoiceManager.catalog.CatalogItemClient;
import hr.unizg.fer.sudec.karlo.invoiceManager.exceptionHandling.FiscalizationGeneralException;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.FiscalizationStatus;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.FiscalizationRequestModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.InvoiceModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.TaxCategoryModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.model.CatalogItemDTO;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.service.InvoiceItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper mapper;
    private final RabbitMqMessageProducer messageProducer;
    private final CatalogItemClient catalogClient;
    private final InvoiceItemService invoiceItemService;

    public List<InvoiceModel> getAllInvoices() {
        return mapper.map(invoiceRepository.findAll(), new TypeToken<List<InvoiceModel>>() {}.getType());
    }
    public List<InvoiceModel> getAllInvoicesWithCatalogItem(Long catalogItemId) {
        return mapper.map(invoiceRepository.findInvoicesByCatalogItemId(catalogItemId), new TypeToken<List<InvoiceModel>>() {}.getType());
    }
    @Transactional
    public InvoiceModel getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new FiscalizationGeneralException("Ne postoji račun s id: " + id));
        InvoiceModel invoiceModel = mapper.map(invoice, InvoiceModel.class);
        invoiceItemService.getInvoiceItemsDetails(invoiceModel.getInvoiceItems());
        invoiceModel.setTaxCategories(getTaxCategoriesForInvoice(invoiceModel.getInvoiceItems()));
        return invoiceModel;
    }

    @Transactional
    public InvoiceModel createInvoice(InvoiceModel model) {
        Invoice invoice = mapper.map(model, Invoice.class);
        invoice.setInvoiceFiscalizationStatus(FiscalizationStatus.NIJE_ZAPOCETO);
        if(invoiceRepository.existsInvoiceByInvoiceNumber(invoice.getInvoiceNumber())){
            throw new FiscalizationGeneralException("Već postoji račun s istim brojem računa!");
        }
        List<Long> existingItems = catalogClient.getCatalogItems(
                model.getInvoiceItems().stream().map(CatalogItemDTO::getCatalogItemId).toList()
        ).stream().map(CatalogItemDTO::getId).toList();
        if(existingItems.size() != model.getInvoiceItems().size()){
            throw new FiscalizationGeneralException("U katalogu ne postoje stavke s id: " +
                    model.getInvoiceItems().stream()
                            .map(CatalogItemDTO::getCatalogItemId)
                            .filter(id -> !existingItems.contains(id))
                            .map(Object::toString)
                            .collect(Collectors.joining(",")));
        }
        invoice.getInvoiceItems().forEach(i -> i.setInvoice(invoice));
        updateInvoiceInTotalField(invoice);
        invoiceRepository.save(invoice);
        return mapper.map(invoice, InvoiceModel.class);
    }

    @Transactional
    public InvoiceModel updateInvoice(Long id, InvoiceModel model) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new FiscalizationGeneralException("Ne postoji račun s id: " + id));
        if(invoice.getInvoiceFiscalizationStatus() == FiscalizationStatus.FISKALIZIRANO
                | invoice.getInvoiceFiscalizationStatus() == FiscalizationStatus.U_OBRADI){
            throw new FiscalizationGeneralException("Fiskalizacija je u tijeku ili je račun već fiskaliziran!");
        }
        mapper.map(model, invoice);
        invoice.getInvoiceItems().forEach(i -> i.setInvoice(invoice));
        updateInvoiceInTotalField(invoice);
        invoiceRepository.save(invoice);
        return mapper.map(invoice, InvoiceModel.class);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    public List<CatalogItemDTO> getAllInvoiceItemsForInvoice(Long invoiceId){
        List<CatalogItemDTO> items = getInvoice(invoiceId).getInvoiceItems();
        invoiceItemService.getInvoiceItemsDetails(items);
        return items;
    }
    @Transactional
    public InvoiceModel startFiscalizationProcess(Long invoiceId){
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new FiscalizationGeneralException("Ne postoji račun s id: " + invoiceId));
        if(invoice.getInvoiceFiscalizationStatus() == FiscalizationStatus.U_OBRADI
                || invoice.getInvoiceFiscalizationStatus() == FiscalizationStatus.FISKALIZIRANO){
            throw new FiscalizationGeneralException("Nije moguće poslati zahtjev za fiskalizaciju jer je račun već fiskaliziran ili u obradi!");
        }

        invoice.setInvoiceFiscalizationStatus(FiscalizationStatus.U_OBRADI);
        messageProducer.publish(
                fromInvoice(invoice),
                FiscalizationQueuesConfig.internalExchange,
                FiscalizationQueuesConfig.toFiscalizationInternalRoutingKey);
        invoiceRepository.save(invoice);
        return mapper.map(invoice, InvoiceModel.class);
    }

    @Transactional
    public void handleFiscalizationResult(String invoiceNumber, boolean success, String result){
        Invoice invoice = invoiceRepository.findInvoiceByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new FiscalizationGeneralException("Ne postoji račun s brojem računa: " + invoiceNumber));
        if(success){
            invoice.setInvoiceFiscalizationStatus(FiscalizationStatus.FISKALIZIRANO);
            String[] splitted = result.split("##kraj##");
            invoice.setJir(splitted[0].replaceFirst("JIR:", ""));
            invoice.setZki(splitted[1].replaceFirst("ZKI:", ""));
        } else {
            invoice.setInvoiceFiscalizationStatus(FiscalizationStatus.NEUSPJESNO_FISKALIZIRANO);
            invoice.setFiscalizationMessage(result);
        }
        invoiceRepository.save(invoice);

        //generate fiscalization qr code => qrCodeService.generateFiscalInvoiceQrCode(invoice);
    }

    public void updateInvoiceInTotalField(Invoice invoice){
        List<TaxCategoryModel> taxCategories = getTaxCategoriesForInvoice(invoice);
        invoice.setInTotal(taxCategories.stream().map(TaxCategoryModel::getIznos).reduce(Double::sum).orElse(null));
    }
    public List<TaxCategoryModel> getTaxCategoriesForInvoice(Invoice invoice){
        List<CatalogItemDTO> invoiceItems = invoice.getInvoiceItems().stream().map(i -> mapper.map(i, CatalogItemDTO.class)).toList();
        invoiceItemService.getInvoiceItemsDetails(invoiceItems);
        return getTaxCategoriesForInvoice(invoiceItems);
    }

    public List<TaxCategoryModel> getTaxCategoriesForInvoice(List<CatalogItemDTO> invoiceItems){
        Map<Double, TaxCategoryModel> taxCategories = new HashMap<>();

        for (CatalogItemDTO invoiceItem: invoiceItems) {
            if(taxCategories.containsKey(invoiceItem.getTaxPercentage())){
                TaxCategoryModel taxCategory = taxCategories.get(invoiceItem.getTaxPercentage());
                taxCategory.addToOsnovica(invoiceItem.getQuantity() * invoiceItem.getGrossPrice());
            } else {
                TaxCategoryModel taxCategory = new TaxCategoryModel();
                taxCategory.setStopaPdv(invoiceItem.getTaxPercentage());
                taxCategory.setOsnovica(invoiceItem.getQuantity() * invoiceItem.getGrossPrice());

                taxCategories.put(invoiceItem.getTaxPercentage(),taxCategory);
            }
        }

        List<TaxCategoryModel> output = new ArrayList<>();
        for (Map.Entry<Double, TaxCategoryModel> entry: taxCategories.entrySet()) {
            TaxCategoryModel finalObject = entry.getValue();
            finalObject.computeIznos();
            output.add(finalObject);
        }
        return output;
    }

    private FiscalizationRequestModel fromInvoice(Invoice invoice){
        return FiscalizationRequestModel.builder()
                .oib("21233832319")
                .brojcanaOznakaRacuna(invoice.getInvoiceNumber())
                .oznakaPoslovnogProstora("1")
                .oznakaNaplatnogUredaja("1")
                .datVrijeme(invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm:ss")))
                .nacinPlacanja(invoice.getPaymentType().getCode())
                .naknadnaDostava(false)
                .oibOperatera("21233832319")
                .oznakaSlijednosti("P")
                .ukupanIznos(invoice.getInTotal())
                .uSustavuPdva(true)
                .obracunatiPorez(getTaxCategoriesForInvoice(invoice))
                .build();
    }
}
