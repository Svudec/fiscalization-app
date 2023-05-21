package hr.unizg.fer.sudec.karlo.catalogManagement.entity;

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
    private Double netPrice;
    private Double quantity;
    private String unit;
    private String taxCategory;
}
