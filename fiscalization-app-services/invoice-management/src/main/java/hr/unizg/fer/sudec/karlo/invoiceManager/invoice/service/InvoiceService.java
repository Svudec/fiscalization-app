package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.FiscalizationStatus;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.InvoiceModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity.InvoiceItem;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.service.InvoiceItemRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper mapper;
    private final InvoiceItemRepository itemRepository;

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
        //TODO: add to fiscalization queue
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

    private void addItemsToInvoice(InvoiceModel model, Invoice invoice) {
        List<InvoiceItem> items = mapper.map(model.getInvoiceItems(), new TypeToken<List<InvoiceItem>>() {}.getType());
        items.forEach(p -> p.setInvoice(invoice));
        invoice.setInvoiceItems(items);
        invoiceRepository.save(invoice);
    }

    public void handleFiscalizationResult(String fiscalizationResult){
        //TODO: handle result (error/success)
        //generate fiscalization qr code => qrCodeService.generateFiscalInvoiceQrCode(invoice);
    }
}
