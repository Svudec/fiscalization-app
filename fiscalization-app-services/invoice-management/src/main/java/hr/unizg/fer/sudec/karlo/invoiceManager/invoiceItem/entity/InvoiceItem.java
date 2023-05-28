package hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "invoice_item",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"invoice_id", "catalog_item_id"})
})
@Getter
@Setter
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "catalog_item_id")
    @NotNull
    private Long catalogItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    @NotNull
    private Invoice invoice;

    @Column(name = "quantity")
    private Double quantity;

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
