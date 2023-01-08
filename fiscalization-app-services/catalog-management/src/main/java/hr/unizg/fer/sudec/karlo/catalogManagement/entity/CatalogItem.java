package hr.unizg.fer.sudec.karlo.catalogManagement.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CatalogItem {
    @Id
    @SequenceGenerator(name = "catalog_item_sequence", sequenceName = "catalog_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalog_id_sequence")
    private Integer id;
    private String label;
    private String price;
    private String vat;
}
