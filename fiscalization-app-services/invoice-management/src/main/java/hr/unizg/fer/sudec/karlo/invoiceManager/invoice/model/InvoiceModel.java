package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.FiscalizationStatus;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.PaymentType;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.model.CatalogItemDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class InvoiceModel {
    private Long id;

    private FiscalizationStatus invoiceFiscalizationStatus;

    private String fiscalizationMessage;

    @NotNull(message = "Invoice date cannot be null.")
    private LocalDateTime invoiceDate;

    @NotNull(message = "Invoice number cannot be null.")
    @Length(min = 1, max = 255, message = "exception.length255")
    private String invoiceNumber;

    private PaymentType paymentType;

    private String createdBy;

    @Length(max = 255, message = "exception.length255")
    private String note;

    private Double inTotal;

    private String jir;

    private String zki;

    private String qrCode;

    @NotEmpty(message = "Invoice needs to have at least one product.")
    @Valid
    private List<CatalogItemDTO> invoiceItems;

    private List<TaxCategoryModel> taxCategories;
}
