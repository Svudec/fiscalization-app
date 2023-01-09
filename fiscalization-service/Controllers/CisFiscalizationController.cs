using System;
using System.Globalization;
using System.Threading.Tasks;
using FiscalizationNetCore.WebApi.Abstractions;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using ServiceReference;

namespace FiscalizationNetCore.WebApi.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class CisFiscalizationController : ControllerBase
    {
        private readonly ILogger<CisFiscalizationController> _logger;
        private readonly IFiscalizationService _fiscalizationService;

        public CisFiscalizationController(ILogger<CisFiscalizationController> logger,
            IFiscalizationService fiscalizationService)
        {
            _logger = logger;
            _fiscalizationService = fiscalizationService;
        }

        [HttpPost("zki")]
        public Object Zki(FiscalizationModel model)
        {
            var invoice = ModelToRacunType(model);

            _fiscalizationService.GenerateZki(invoice);

            return new
            {
                zki = invoice.ZastKod
            };
        }

        [HttpPost]
        public async Task<ObjectResult> Post(FiscalizationModel model)
        {
            var invoice = ModelToRacunType(model);

            var result = await _fiscalizationService.SendInvoiceAsync(invoice);
            result.Zki = invoice.ZastKod;

            Console.WriteLine($"Zki: {result.Zki}, Jir: {result.Jir}");

            if (result.Greske != null)
            {
                return BadRequest(result);
            }

            return Ok(result);
        }

        #region Helpers

        private static RacunType ModelToRacunType(FiscalizationModel model)
        {
            Enum.TryParse(model.nacinPlacanja, out NacinPlacanjaType nacinPlacanja);
            Enum.TryParse(model.oznakaSlijednosti, out OznakaSlijednostiType oznakaSlijednosti);

            var invoice = new RacunType
            {
                BrRac = new BrojRacunaType
                {
                    BrOznRac = model.brojcanaOznakaRacuna,
                    OznPosPr = model.oznakaPoslovnogProstora,
                    OznNapUr = model.oznakaNaplatnogUredaja
                },
                DatVrijeme = model.datVrijeme,
                IznosUkupno = model.ukupanIznos.ToString("0.00", CultureInfo.InvariantCulture),
                NacinPlac = nacinPlacanja,
                NakDost = model.naknadnaDostava,
                Oib = model.Oib,
                OibOper = model.Oib,
                OznSlijed = oznakaSlijednosti,
                Pdv = new[]
                {
                    new PorezType
                    {
                        Stopa = model.stopaPdv.ToString("0.00", CultureInfo.InvariantCulture),
                        Osnovica = model.osnovica.ToString("0.00", CultureInfo.InvariantCulture),
                        Iznos = model.iznos.ToString("0.00", CultureInfo.InvariantCulture),
                    }
                },
                USustPdv = model.uSustavuPdva
            };
            return invoice;
        }
        #endregion
    }
}