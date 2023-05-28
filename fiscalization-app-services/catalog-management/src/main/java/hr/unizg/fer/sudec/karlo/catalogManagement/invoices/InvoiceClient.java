package hr.unizg.fer.sudec.karlo.catalogManagement.invoices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "invoice", url = "${clients.invoice-management.url}")
public interface InvoiceClient {
    @GetMapping(path = "${baseUrl.invoice}/all")
    List<InvoiceModel> getInvoicesWithItem(@RequestParam("catalogItemId") Long catalogItemId);
}
