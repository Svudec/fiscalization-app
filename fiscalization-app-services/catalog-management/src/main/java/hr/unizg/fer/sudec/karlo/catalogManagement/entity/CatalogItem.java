package hr.unizg.fer.sudec.karlo.catalogManagement.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "catalog_item")
@Getter
@Setter
public class CatalogItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "product_number")
    private String productNumber;

    @Column(name = "gross_price")
    private Double grossPrice;

    @Column(name = "tax_percentage")
    private Double taxPercentage;

    @Column(name = "net_price")
    private Double netPrice;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit")
    private String unit;

    @Column(name = "tax_category")
    private String taxCategory;
}
