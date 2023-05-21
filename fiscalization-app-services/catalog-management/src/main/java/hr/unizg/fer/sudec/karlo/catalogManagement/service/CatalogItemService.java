package hr.unizg.fer.sudec.karlo.catalogManagement.service;

import hr.unizg.fer.sudec.karlo.catalogManagement.entity.CatalogItem;
import hr.unizg.fer.sudec.karlo.catalogManagement.entity.CatalogItemDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CatalogItemService {
    private final CatalogItemRepository itemRepository;
    private final ModelMapper mapper;
    public void createItem(CatalogItemDTO dto) {
        CatalogItem newItem = new CatalogItem();
        mapper.map(dto, newItem);
        itemRepository.save(newItem);
    }
}
