package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

public enum FiscalizationStatus{
        FISKALIZIRANO("DA"), NEUSPJESNO_FISKALIZIRANO("NE"), U_OBRADI("OBRADA");

        private final String code;

        FiscalizationStatus(String code){
                this.code = code;
        }

        public String getCode() {
                return code;
        }
}
