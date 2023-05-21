package hr.unizg.fer.sudec.karlo.catalogManagement.service;

import hr.unizg.fer.sudec.karlo.catalogManagement.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
}
