package hr.unizg.fer.sudec.karlo.catalogManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItem {
    private Integer id;
    private String label;
    private String price;
    private String vat;
}
