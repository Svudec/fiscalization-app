package hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.service;

import hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
    boolean existsByProductNumberAndIsActiveIsTrue(String productNumber);
    List<CatalogItem> findAllByIsActiveIsTrueOrderByProductNumber();
    Optional<CatalogItem> findByIdAndIsActiveIsTrue(Long id);
}
