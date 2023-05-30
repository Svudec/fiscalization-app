package hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.controller;

import hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.model.CatalogItemDTO;
import hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.service.CatalogItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${baseUrl.catalog}")
@AllArgsConstructor
public class CatalogItemController {

    private final CatalogItemService itemService;

    @GetMapping("/all")
    public List<CatalogItemDTO> getAllItems(@RequestParam(name = "ids", required = false) List<Long> ids){
        return ids == null ? itemService.getItems() : itemService.getItemsWithId(ids);
    }
    @GetMapping("/{id}")
    public CatalogItemDTO getItem(@PathVariable Long id){ return itemService.getItem(id); }
    @PostMapping
    public CatalogItemDTO addItem(@RequestBody CatalogItemDTO dto){
    return itemService.createItem(dto);
    }

    @PutMapping
    public CatalogItemDTO updateItem(@RequestBody CatalogItemDTO dto){ return itemService.updateItem(dto); }
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id){ itemService.deleteItem(id); }
}
