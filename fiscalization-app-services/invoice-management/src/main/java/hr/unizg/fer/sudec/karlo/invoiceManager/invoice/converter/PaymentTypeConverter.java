package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.converter;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.PaymentType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class PaymentTypeConverter implements AttributeConverter<PaymentType, String> {

    @Override
    public String convertToDatabaseColumn(PaymentType paymentType) {
        if (paymentType == null) {
            return null;
        }
        return paymentType.getCode();
    }

    @Override
    public PaymentType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(PaymentType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
