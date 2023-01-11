package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.converter;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.FiscalizationStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class FiscalizationStatusConverter implements AttributeConverter<FiscalizationStatus, String> {
    @Override
    public String convertToDatabaseColumn(FiscalizationStatus fiscalizationStatus) {
        if (fiscalizationStatus == null) {
            return null;
        }
        return fiscalizationStatus.getCode();
    }

    @Override
    public FiscalizationStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(FiscalizationStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
