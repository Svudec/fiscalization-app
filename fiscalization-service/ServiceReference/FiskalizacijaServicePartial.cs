using System.Threading.Tasks;
using System.Xml.Serialization;
using FiscalizationNetCore.WebApi.Services;

namespace ServiceReference
{
    public partial class RacunZahtjev : FiscalizationService.ICisRequest { }

    public partial class RacunOdgovor : FiscalizationService.ICisResponse
    {
        [XmlIgnore]
        public FiscalizationService.ICisRequest Request { get; set; }
    }

    public partial class ProvjeraZahtjev : FiscalizationService.ICisRequest { }

    public partial class ProvjeraOdgovor : FiscalizationService.ICisResponse
    {
        [XmlIgnore]
        public FiscalizationService.ICisRequest Request { get; set; }
    }
    
    public partial class FiskalizacijaPortTypeClient
    {
        public async Task<RacunOdgovor> RacuniAsync(RacunZahtjev request)
        {
            var racun = await racuniAsync(request);
            return racun.RacunOdgovor;
        }

        public async Task<ProvjeraOdgovor> ProvjeraAsync(ProvjeraZahtjev request)
        {
            var provjera = await provjeraAsync(request);
            return provjera.ProvjeraOdgovor;
        }

        public async Task<string> EchoAsync(string request)
        {
            var echo = await echoAsync(request);
            return echo.EchoResponse;
        }
    }
}