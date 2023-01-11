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
    }
}