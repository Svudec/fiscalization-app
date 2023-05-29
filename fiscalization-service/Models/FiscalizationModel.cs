using ServiceReference;
using System.Globalization;
using System;
using FiscalizationNetCore.WebApi.Models;
using System.Linq;

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
        public bool uSustavuPdva { get; set; }
        public double ukupanIznos { get; set; }
        public TaxCategoryModel[] obracunatiPorez { get; set; }

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
                Pdv = obracunatiPorez.Select(category => new PorezType
                {
                    Stopa = category.stopaPdv.ToString("0.00", CultureInfo.InvariantCulture),
                    Osnovica = category.osnovica.ToString("0.00", CultureInfo.InvariantCulture),
                    Iznos = category.iznos.ToString("0.00", CultureInfo.InvariantCulture)
                }).ToArray(),
                USustPdv = this.uSustavuPdva
            };
            return invoice;
        }
    }
}