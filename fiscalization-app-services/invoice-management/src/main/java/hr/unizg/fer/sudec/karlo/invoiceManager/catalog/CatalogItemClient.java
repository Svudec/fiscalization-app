package hr.unizg.fer.sudec.karlo.invoiceManager.catalog;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.model.CatalogItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "catalogItem", url = "${clients.catalog-management.url}")
public interface CatalogItemClient {

    @GetMapping(path = "${baseUrl.catalog}/all")
    List<CatalogItemDTO> getCatalogItems(@RequestParam(name = "ids", required = false) Iterable<Long> ids);
    @GetMapping(path = "${baseUrl.catalog}/all")
    List<CatalogItemDTO> getCatalogItems();
}
