package hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.service;

import hr.unizg.fer.sudec.karlo.catalogManagement.entity.CatalogItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "catalogItem", url = "http://localhost:8081")
public interface InvoiceItemClient {

    @GetMapping(path = "api/v1/catalog-item/all")
    List<CatalogItemDTO> getCatalogItems();
}
