package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity.InvoiceItem;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

    @Column(name = "operator_id")
    private String operatorId;

    @Column(name = "operator_oib")
    private String operatorOIB;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceItem> invoiceItems;

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
