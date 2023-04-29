using System;
using System.Threading.Tasks;
using FiscalizationNetCore.WebApi.Abstractions;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

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
            var invoice = model.ToRacunType();

            _fiscalizationService.GenerateZki(invoice);

            return new
            {
                zki = invoice.ZastKod
            };
        }

        [HttpPost]
        public async Task<ObjectResult> Post(FiscalizationModel model)
        {
            var invoice = model.ToRacunType();

            var result = await _fiscalizationService.SendInvoiceAsync(invoice);
            result.Zki = invoice.ZastKod;

            Console.WriteLine($"Zki: {result.Zki}, Jir: {result.Jir}");

            if (result.Greske != null)
            {
                return BadRequest(result);
            }

            return Ok(result);
        }
    }
}