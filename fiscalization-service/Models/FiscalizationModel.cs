using ServiceReference;
using System.Globalization;
using System;

namespace FiscalizationNetCore.WebApi
{
    public class FiscalizationModel
    {
        public string Oib { get; set; } = "21233832319"; //FIRA OIB

        public string brojcanaOznakaRacuna { get; set; }

        public string oznakaPoslovnogProstora { get; set; }
        
        public string oznakaNaplatnogUredaja { get; set; }

        public string datVrijeme { get; set; }

        public string nacinPlacanja { get; set; }

        public bool naknadnaDostava { get; set; }

        public string oibOperatera { get; set; }

        public string oznakaSlijednosti { get; set; }

        public double stopaPdv { get; set; }

        public double osnovica { get; set; }

        public double iznos { get; set; }

        public bool uSustavuPdva { get; set; }

        public double ukupanIznos { get; set; }

        public RacunType ToRacunType()
        {
            Enum.TryParse(this.nacinPlacanja, out NacinPlacanjaType nacinPlacanja);
            Enum.TryParse(this.oznakaSlijednosti, out OznakaSlijednostiType oznakaSlijednosti);

            var invoice = new RacunType
            {
                BrRac = new BrojRacunaType
                {
                    BrOznRac = this.brojcanaOznakaRacuna,
                    OznPosPr = this.oznakaPoslovnogProstora,
                    OznNapUr = this.oznakaNaplatnogUredaja
                },
                DatVrijeme = this.datVrijeme,
                IznosUkupno = this.ukupanIznos.ToString("0.00", CultureInfo.InvariantCulture),
                NacinPlac = nacinPlacanja,
                NakDost = this.naknadnaDostava,
                Oib = this.Oib,
                OibOper = this.Oib,
                OznSlijed = oznakaSlijednosti,
                Pdv = new[]
                {
                    new PorezType
                    {
                        Stopa = this.stopaPdv.ToString("0.00", CultureInfo.InvariantCulture),
                        Osnovica = this.osnovica.ToString("0.00", CultureInfo.InvariantCulture),
                        Iznos = this.iznos.ToString("0.00", CultureInfo.InvariantCulture),
                    }
                },
                USustPdv = this.uSustavuPdva
            };
            return invoice;
        }
    }
}