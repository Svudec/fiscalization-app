using System.Threading.Tasks;
using ServiceReference;

namespace FiscalizationNetCore.WebApi.Abstractions
{
    public interface IFiscalizationService
    {
        Task<RacunOdgovor> SendInvoiceAsync(RacunType invoice);
        void GenerateZki(RacunType invoice);

    }
}