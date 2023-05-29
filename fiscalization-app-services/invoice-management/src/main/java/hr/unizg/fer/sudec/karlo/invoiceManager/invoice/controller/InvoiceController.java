package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.controller;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.InvoiceModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service.InvoiceService;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.model.CatalogItemDTO;
import hr.unizg.fer.sudec.karlo.invoiceManager.catalog.CatalogItemClient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("${baseUrl.invoice}")
@AllArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

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

    @GetMapping("/{id}/items")
    public List<CatalogItemDTO> getItemsForInvoice(@PathVariable("id") Long id){ return invoiceService.getAllInvoiceItemsForInvoice(id); }

    @DeleteMapping("/{id}")
    public void deleteInvoice(@PathVariable("id") Long id) {
        invoiceService.deleteInvoice(id);
    }

    @GetMapping("/all")
    public List<InvoiceModel> getAllInvoices(@RequestParam(required = false, name = "catalogItemId") Long catalogItemId) {
        return catalogItemId == null ? invoiceService.getAllInvoices() : invoiceService.getAllInvoicesWithCatalogItem(catalogItemId);
    }
}
