package hr.unizg.fer.sudec.karlo.catalogManagement.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CatalogItemDTO {
    private Integer id;
    private String label;
    private String price;
    private String vat;
}
