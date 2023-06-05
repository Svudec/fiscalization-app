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
        if(dto.getId() != null && itemRepository.existsByProductNumberAndIsActiveIsTrue(dto.getProductNumber())){
            throw new FiscalizationGeneralException("Već postoji proizvod s identifikatorom: " + dto.getProductNumber());
        }
        CatalogItem newItem = mapper.map(dto, CatalogItem.class);
        newItem.setIsActive(true);
        itemRepository.save(newItem);
        return mapper.map(newItem, CatalogItemDTO.class);
    }

    public CatalogItemDTO getItem(Long id){
        CatalogItem item = itemRepository.findById(id)
                .orElseThrow(() -> new FiscalizationGeneralException("Ne postoji proizvod s id: " + id));
        return mapper.map(item, CatalogItemDTO.class);
    }
    public List<CatalogItemDTO> getItems(){
        return itemRepository.findAllByIsActiveIsTrue().stream().map((element) -> mapper.map(element, CatalogItemDTO.class)).collect(Collectors.toList());
    }

    public List<CatalogItemDTO> getItemsWithId(Iterable<Long> ids){
        return itemRepository.findAllById(ids).stream().map((element) -> mapper.map(element, CatalogItemDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public CatalogItemDTO updateItem(CatalogItemDTO dto){
        CatalogItem item = itemRepository.findByIdAndIsActiveIsTrue(dto.getId())
                .orElseThrow(() -> new FiscalizationGeneralException("Ne postoji proizvod ili nije aktivan s id: " + dto.getId()));
        if(!dto.getProductNumber().equals(item.getProductNumber()) && itemRepository.existsByProductNumberAndIsActiveIsTrue(dto.getProductNumber())){
            throw new FiscalizationGeneralException("Već postoji proizvod s identifikatorom: " + dto.getProductNumber());
        }

        // if it is used in invoices, create new one instead of updating
        if(invoiceClient.getInvoicesWithItem(item.getId()).isEmpty()){
            mapper.map(dto, item);
            item.setIsActive(true);
            itemRepository.save(item);
            return mapper.map(item, CatalogItemDTO.class);
        }
        item.setIsActive(false);
        itemRepository.save(item);
        dto.setId(null);
        return createItem(dto);
    }

    @Transactional
    public void deleteItem(Long itemId){
        CatalogItem item = itemRepository.findByIdAndIsActiveIsTrue(itemId)
                .orElseThrow(() -> new FiscalizationGeneralException("Ne postoji proizvod ili nije aktivan s id: " + itemId));
        // if it is used in invoices, just set isActive to false
        if(invoiceClient.getInvoicesWithItem(item.getId()).isEmpty()){
            itemRepository.delete(item);
        }
        else {
            item.setIsActive(false);
            itemRepository.save(item);
        }
    }
}
