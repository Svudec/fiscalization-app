package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

public enum PaymentType{
    GOTOVINA("G"),
    KARTICA("K"),
    CEK("C"),
    TRANSAKCIJSKI_RACUN("T"),
    OSTALO("O");
    private final String code;
    PaymentType(String code){
        this.code = code;
    }
    public String getCode() {
        return code;
    }
}
