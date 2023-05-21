package hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "invoice_item")
@Getter
@Setter
public class InvoiceItem {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InvoiceItem that = (InvoiceItem) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
