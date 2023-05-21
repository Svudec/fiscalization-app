package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service;

import hr.unizg.fer.sudec.karlo.amqp.RabbitMqMessageProducer;
import hr.unizg.fer.sudec.karlo.amqp.config.FiscalizationQueuesConfig;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.FiscalizationStatus;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.FiscalizationRequestModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.InvoiceModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity.InvoiceItem;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.service.InvoiceItemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper mapper;
    private final InvoiceItemRepository itemRepository;
    private final RabbitMqMessageProducer messageProducer;

    public List<InvoiceModel> getAllInvoices() {
        return mapper.map(invoiceRepository.findAll(), new TypeToken<List<InvoiceModel>>() {}.getType());
    }
    @Transactional
    public InvoiceModel getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invoice not found!"));
        return mapper.map(invoice, InvoiceModel.class);
    }

    @Transactional
    public InvoiceModel createInvoice(InvoiceModel model) {
        Invoice invoice = mapper.map(model, Invoice.class);
        invoice.setInvoiceFiscalizationStatus(FiscalizationStatus.NIJE_ZAPOCETO);
        invoiceRepository.save(invoice);
        addItemsToInvoice(model, invoice);
        return mapper.map(invoice, InvoiceModel.class);
    }

    @Transactional
    public InvoiceModel updateInvoice(Long id, InvoiceModel model) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invoice not found!"));
        mapper.map(model, invoice);
        itemRepository.deleteAllByInvoice(invoice);
        addItemsToInvoice(model, invoice);
        return mapper.map(invoice, InvoiceModel.class);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    @Transactional
    public InvoiceModel startFiscalizationProcess(Long invoiceId){
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find invoice with id: " + invoiceId));
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
                .orElseThrow(() -> new EntityNotFoundException("Can't find invoice with number: " + invoiceNumber));
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

    private void addItemsToInvoice(InvoiceModel model, Invoice invoice) {
        List<InvoiceItem> items = mapper.map(model.getInvoiceItems(), new TypeToken<List<InvoiceItem>>() {}.getType());
        items.forEach(p -> p.setInvoice(invoice));
        invoice.setInvoiceItems(items);
        invoiceRepository.save(invoice);
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
                .stopaPdv(25.0)
                .osnovica(12.0)
                .iznos(3.0)
                .ukupanIznos(15.0)
                .uSustavuPdva(true)
                .build();
    }
}
