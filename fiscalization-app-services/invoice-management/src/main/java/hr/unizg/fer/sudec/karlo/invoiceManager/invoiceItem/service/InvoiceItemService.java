package hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.service;

import hr.unizg.fer.sudec.karlo.invoiceManager.catalog.CatalogItemClient;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.model.CatalogItemDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class InvoiceItemService {

    private final CatalogItemClient catalogItemClient;
    private final ModelMapper modelMapper;
    public void getInvoiceItemsDetails(List<CatalogItemDTO> invoiceItems){
        List<CatalogItemDTO> fetchedItems = catalogItemClient.getCatalogItems(invoiceItems.stream().map(CatalogItemDTO::getCatalogItemId).toList());
        // fix ids
        fetchedItems.forEach(i -> {
            i.setCatalogItemId(i.getId());
            i.setId(null);
        });
        // map to original list
        for (CatalogItemDTO original: invoiceItems) {
            Optional<CatalogItemDTO> fetched = fetchedItems.stream().filter(i -> i.getCatalogItemId().equals(original.getCatalogItemId()))
                    .findFirst();
            fetched.ifPresent(fetchedItem -> modelMapper.map(fetchedItem, original));
        }
    }
}
