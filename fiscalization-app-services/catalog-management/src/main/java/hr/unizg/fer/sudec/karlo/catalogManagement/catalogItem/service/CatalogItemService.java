package hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.service;

import hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.entity.CatalogItem;
import hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.model.CatalogItemDTO;
import hr.unizg.fer.sudec.karlo.catalogManagement.exceptionHandling.FiscalizationGeneralException;
import hr.unizg.fer.sudec.karlo.catalogManagement.invoices.InvoiceClient;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CatalogItemService {
    private final CatalogItemRepository itemRepository;
    private final ModelMapper mapper;
    private final InvoiceClient invoiceClient;
    @Transactional
    public CatalogItemDTO createItem(CatalogItemDTO dto) {
        if(dto.getId() != null && itemRepository.existsById(dto.getId())){
            throw new FiscalizationGeneralException("Već postoji stavka s id: " + dto.getId());
        }
        CatalogItem newItem = mapper.map(dto, CatalogItem.class);
        itemRepository.save(newItem);
        return mapper.map(newItem, CatalogItemDTO.class);
    }

    public List<CatalogItemDTO> getItems(){
        return itemRepository.findAll().stream().map((element) -> mapper.map(element, CatalogItemDTO.class)).collect(Collectors.toList());
    }

    public List<CatalogItemDTO> getItemsWithId(Iterable<Long> ids){
        return itemRepository.findAllById(ids).stream().map((element) -> mapper.map(element, CatalogItemDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public CatalogItemDTO updateItem(CatalogItemDTO dto){
        CatalogItem item = itemRepository.findById(dto.getId())
                .orElseThrow(() -> new FiscalizationGeneralException("Catalog Item with that id does not exist!"));
        // if it is used in invoices, create new one instead of updating
        if(!invoiceClient.getInvoicesWithItem(item.getId()).isEmpty()){
            dto.setId(null);
            return createItem(dto);
        }
        mapper.map(dto, item);
        itemRepository.save(item);
        return mapper.map(item, CatalogItemDTO.class);
    }
}
