package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity;

public enum FiscalizationStatus{
        FISKALIZIRANO("F"), NEUSPJEŠNO_FISKALIZIRANO("NF"), U_OBRADI("O");

        private final String code;

        private FiscalizationStatus(String code){
                this.code = code;
        }

        public String getCode() {
                return code;
        }
}
