package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

public enum FiscalizationStatus{
        FISKALIZIRANO("DA"), NEUSPJESNO_FISKALIZIRANO("NE"), U_OBRADI("OBRADA"), NIJE_ZAPOCETO("NIJE_ZAPOCETO");

        private final String code;

        FiscalizationStatus(String code){
                this.code = code;
        }

        public String getCode() {
                return code;
        }
}
