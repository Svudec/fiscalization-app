package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.controller;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.InvoiceModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/invoice")
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

    @DeleteMapping("/{id}")
    public void deleteInvoice(@PathVariable("id") Long id) {
        invoiceService.deleteInvoice(id);
    }

    @GetMapping("/all")
    public List<InvoiceModel> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }
}
