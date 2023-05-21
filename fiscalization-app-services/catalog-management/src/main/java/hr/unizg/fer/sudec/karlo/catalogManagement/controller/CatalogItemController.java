package hr.unizg.fer.sudec.karlo.catalogManagement.controller;

import hr.unizg.fer.sudec.karlo.catalogManagement.entity.CatalogItemDTO;
import hr.unizg.fer.sudec.karlo.catalogManagement.service.CatalogItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/catalog-item")
@AllArgsConstructor
public class CatalogItemController {

    private final CatalogItemService itemService;

    @GetMapping("/all")
    public List<CatalogItemDTO> getAllItems(){
        return itemService.getItems();
    }

    @PostMapping
    public CatalogItemDTO addItem(@RequestBody CatalogItemDTO dto){
    return itemService.createItem(dto);
    }
}
