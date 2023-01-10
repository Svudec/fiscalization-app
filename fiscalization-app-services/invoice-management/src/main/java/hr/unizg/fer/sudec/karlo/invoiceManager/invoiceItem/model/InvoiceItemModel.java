package hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class InvoiceItemModel {
    private Long id;

    @NotBlank(message = "Name cannot be null.")
    @Length(min = 1, max = 255, message = "exception.length255")
    private String name;

    @Length(max = 10000, message = "exception.length10000")
    private String description;

    @Length(max = 255, message = "exception.length255")
    private String productNumber;

    @NotNull(message = "Gross price cannot be null.")
    private Double grossPrice;

    @NotNull(message = "Tax percentage cannot be empty.")
    private Double taxPercentage;

    @NotNull(message = "Net price cannot be null.")
    private Double netPrice;

    @NotNull(message = "Quantity cannot be null.")
    private Double quantity;

    @Length(max = 255, message = "exception.length255")
    private String unit;

    @Length(max = 255, message = "exception.length255")
    private String taxCategory;
}
