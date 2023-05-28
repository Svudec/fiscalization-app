package hr.unizg.fer.sudec.karlo.catalogManagement.invoices;

import hr.unizg.fer.sudec.karlo.catalogManagement.catalogItem.model.CatalogItemDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class InvoiceModel {
    private Long id;

    private String invoiceFiscalizationStatus;

    private LocalDateTime invoiceDate;

    private String invoiceNumber;

    private String paymentType;

    private String createdBy;

    private String operatorId;

    private String operatorOIB;

    private String note;

    private Double inTotal;

    private String jir;

    private String zki;

    private String qrCode;

    private List<CatalogItemDTO> invoiceItems;
}
