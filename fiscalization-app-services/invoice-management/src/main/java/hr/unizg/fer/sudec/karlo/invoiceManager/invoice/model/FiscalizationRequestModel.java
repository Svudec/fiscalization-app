package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FiscalizationRequestModel{

    private String oib;
    private String brojcanaOznakaRacuna;
    private String oznakaPoslovnogProstora;
    private String oznakaNaplatnogUredaja;
    private String datVrijeme;
    private String nacinPlacanja;
    private Boolean naknadnaDostava;
    private String oibOperatera;
    private String oznakaSlijednosti;
    private Boolean uSustavuPdva;
    private Double ukupanIznos;
    private List<TaxCategoryModel> obracunatiPorez;
}
