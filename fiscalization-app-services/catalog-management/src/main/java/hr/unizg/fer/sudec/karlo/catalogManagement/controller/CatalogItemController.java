package hr.unizg.fer.sudec.karlo.catalogManagement.controller;

import hr.unizg.fer.sudec.karlo.catalogManagement.entity.CatalogItemDTO;
import hr.unizg.fer.sudec.karlo.catalogManagement.service.CatalogItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/catalog-item")
@AllArgsConstructor
public class CatalogItemController {

    private final CatalogItemService itemService;

    @PostMapping
    public void addItem(@RequestBody CatalogItemDTO dto){
    log.info("Creating new catalog item {}", dto);
    itemService.createItem(dto);
    }
}
