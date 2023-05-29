package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TaxCategoryModel {
    private double stopaPdv;
    private double osnovica;
    private double iznos;

    public void computeIznos(){
        iznos = osnovica + (osnovica * stopaPdv);
    }

    public void addToOsnovica(double valueToAdd){
        osnovica += valueToAdd;
    }
}
