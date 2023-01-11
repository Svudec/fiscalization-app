package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity.InvoiceItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_fiscalization_status")
    private FiscalizationStatus invoiceFiscalizationStatus;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "invoice_number")
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
}
