package hr.unizg.fer.sudec.karlo.catalogManagement.service;

import hr.unizg.fer.sudec.karlo.catalogManagement.entity.CatalogItem;
import hr.unizg.fer.sudec.karlo.catalogManagement.entity.CatalogItemDTO;
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
    @Transactional
    public CatalogItemDTO createItem(CatalogItemDTO dto) {
        CatalogItem newItem = mapper.map(dto, CatalogItem.class);
        itemRepository.save(newItem);
        return mapper.map(newItem, CatalogItemDTO.class);
    }

    public List<CatalogItemDTO> getItems(){
        return itemRepository.findAll().stream().map((element) -> mapper.map(element, CatalogItemDTO.class)).collect(Collectors.toList());
    }
}
