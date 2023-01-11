package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

public enum PaymentType{
    GOTOVINA("G"), KARTICA("K"), OSTALO("O");
    private final String code;
    private PaymentType(String code){
        this.code = code;
    }
    public String getCode() {
        return code;
    }
}
