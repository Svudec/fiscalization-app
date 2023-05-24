package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.controller;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.InvoiceModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service.InvoiceService;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity.CatalogItemDTO;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.service.CatalogItemClient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final CatalogItemClient catalogItemClient;

    @GetMapping("/{id}")
    public InvoiceModel getInvoice(@PathVariable("id") Long id) {
        return invoiceService.getInvoice(id);
    }

    @PostMapping
    public InvoiceModel createInvoice(@Valid @RequestBody InvoiceModel model) {
        return invoiceService.createInvoice(model);
    }

    @PutMapping("/{id}")
    public InvoiceModel updateInvoice(@PathVariable("id") Long id, @Valid @RequestBody InvoiceModel model) {
        return invoiceService.updateInvoice(id, model);
    }

    @PutMapping("/{id}/fiscalize")
    public InvoiceModel startFiscalization(@PathVariable("id") Long id) {
        return invoiceService.startFiscalizationProcess(id);
    }

    @DeleteMapping("/{id}")
    public void deleteInvoice(@PathVariable("id") Long id) {
        invoiceService.deleteInvoice(id);
    }

    @GetMapping("/all")
    public List<InvoiceModel> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/items")
    public List<CatalogItemDTO> getItemsForInvoice(){return catalogItemClient.getCatalogItems();}
}
