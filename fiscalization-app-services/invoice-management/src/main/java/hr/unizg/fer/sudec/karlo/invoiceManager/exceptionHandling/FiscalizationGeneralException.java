package hr.unizg.fer.sudec.karlo.invoiceManager.exceptionHandling;

public class FiscalizationGeneralException extends RuntimeException{
    public FiscalizationGeneralException(String message) {
        super(message);
    }

    public FiscalizationGeneralException(String message, Throwable cause) {
        super(message, cause);
    }
}
