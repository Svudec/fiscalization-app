package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private FiscalizationStatus invoiceFiscalizationStatus;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private PaymentType paymentType;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "operator_id")
    private String operatorId;

    @Column(name = "operator_oib")
    private String operatorOIB;

    @Column(name = "note")
    private String note;

    @Column(name = "price_format")
    private String priceFormat;

    @Column(name = "in_total")
    private Double inTotal;

    @Column(name = "jir")
    private String jir;

    @Column(name = "zki")
    private String zki;

    @Column(name = "qr_code")
    private String qrCode;
}
