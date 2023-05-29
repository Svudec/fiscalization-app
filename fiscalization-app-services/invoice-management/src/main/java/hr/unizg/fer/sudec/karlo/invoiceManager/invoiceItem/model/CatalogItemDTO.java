package hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// THIS IS MODEL FOR OBJECT FROM catalog-management service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemDTO {
    private Long id;
    private Long catalogItemId;
    private String name;
    private String description;
    private String productNumber;
    private Double grossPrice;
    private Double taxPercentage;
    private String unit;
    private Double quantity;
}
