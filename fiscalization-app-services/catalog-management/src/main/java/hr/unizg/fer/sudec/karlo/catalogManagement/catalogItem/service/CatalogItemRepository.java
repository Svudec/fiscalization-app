package hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.service;

import hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
}
