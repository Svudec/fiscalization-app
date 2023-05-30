package hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "catalog_item")
@Getter
@Setter
public class CatalogItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank
    @NotNull
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "product_number")
    @NotBlank
    @NotNull
    private String productNumber;

    @Column(name = "gross_price")
    @NotNull
    private Double grossPrice;

    @Column(name = "tax_percentage")
    @NotNull
    private Double taxPercentage;

    @Column(name = "unit")
    @NotNull
    @NotBlank
    private String unit;

    @Column(name = "is_active")
    @NotNull
    @ColumnDefault("TRUE")
    private Boolean isActive;
}
