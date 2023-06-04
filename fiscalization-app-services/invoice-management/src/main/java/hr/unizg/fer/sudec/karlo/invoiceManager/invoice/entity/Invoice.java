package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity.InvoiceItem;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "invoice")
@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_fiscalization_status")
    private FiscalizationStatus invoiceFiscalizationStatus;

    @Column(name = "invoice_fiscalization_message")
    private String fiscalizationMessage;

    @Column(name = "invoice_date")
    @NotNull
    private LocalDateTime invoiceDate;

    @Column(name = "invoice_number", unique = true)
    @NotBlank
    private String invoiceNumber;

    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "note")
    private String note;

    @Column(name = "in_total")
    private Double inTotal;

    @Column(name = "jir")
    private String jir;

    @Column(name = "zki")
    private String zki;

    @Column(name = "qr_code")
    private String qrCode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> invoiceItems;

    // custom setter for invoice items because of Hibernate orphan remove,
    // it should never create new list but manipulate existing one
    public void setInvoiceItems(List<InvoiceItem> newItems){
        if(this.invoiceItems == null) { this.invoiceItems = new ArrayList<>(); }
        if(newItems.isEmpty()){
            this.invoiceItems.clear();
            return;
        }

        List<InvoiceItem> newItems2 = new ArrayList<>(newItems);
        List<InvoiceItem> forDelete = new ArrayList<>();
        this.invoiceItems.forEach(item -> {
            boolean shouldDelete = true;
            for (int i = 0; i < newItems2.size(); i++) {
                InvoiceItem newItem = newItems2.get(i);
                if (item.getCatalogItemId().equals(newItem.getCatalogItemId())) {
                    shouldDelete = false;
                    item.setQuantity(newItem.getQuantity());
                    newItems2.remove(i);
                    break;
                }
            }
            if(shouldDelete) { forDelete.add(item); }
        });
        this.invoiceItems.removeAll(forDelete);
        newItems2.forEach(i -> {
            i.setInvoice(this);
            i.setId(null);
        });
        this.invoiceItems.addAll(newItems2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Invoice invoice = (Invoice) o;
        return getId() != null && Objects.equals(getId(), invoice.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
