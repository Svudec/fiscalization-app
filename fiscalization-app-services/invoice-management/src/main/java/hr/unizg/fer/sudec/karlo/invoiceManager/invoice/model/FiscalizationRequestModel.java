package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

    private Double stopaPdv;

    private Double osnovica;

    private Double iznos;

    private Boolean uSustavuPdva;

    private Double ukupanIznos;

}
