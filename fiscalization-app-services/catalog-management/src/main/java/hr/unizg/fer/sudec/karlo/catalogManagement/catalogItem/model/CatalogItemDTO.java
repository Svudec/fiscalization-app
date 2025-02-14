package hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemDTO {
    private Long id;
    private String name;
    private String description;
    private String productNumber;
    private Double grossPrice;
    private Double taxPercentage;
    private String unit;
    private Boolean isActive;
}
