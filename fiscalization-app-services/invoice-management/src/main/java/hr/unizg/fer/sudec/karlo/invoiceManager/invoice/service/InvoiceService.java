package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service;

import hr.unizg.fer.sudec.karlo.amqp.RabbitMqMessageProducer;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.FiscalizationStatus;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.FiscalizationRequestModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.FiscalizationResultModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.InvoiceModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity.InvoiceItem;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.service.InvoiceItemRepository;
import hr.unizg.fer.sudec.karlo.invoiceManager.messageQueue.QueueParamsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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

    private final QueueParamsService queueParamsService;

    public List<InvoiceModel> getAllInvoices() {
        return mapper.map(invoiceRepository.findAll(), new TypeToken<List<InvoiceModel>>() {}.getType());
    }
    @Transactional
    public InvoiceModel getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invoice not found!"));
        return mapper.map(invoice, InvoiceModel.class);
    }

    public InvoiceModel createInvoice(InvoiceModel model) {
        Invoice invoice = mapper.map(model, Invoice.class);
        invoice.setInvoiceFiscalizationStatus(FiscalizationStatus.U_OBRADI);
        invoiceRepository.save(invoice);
        addItemsToInvoice(model, invoice);
        messageProducer.publish(
                fromInvoice(invoice),
                queueParamsService.getInternalExchange(),
                queueParamsService.getToFiscalizationInternalRoutingKey());
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

    public void handleFiscalizationResult(FiscalizationResultModel fiscalizationResult){
        log.info("Received result: {}", fiscalizationResult);
        //TODO: handle result (error/success)
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
