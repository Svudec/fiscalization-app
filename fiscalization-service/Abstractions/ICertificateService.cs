using System.Security.Cryptography.X509Certificates;

namespace FiscalizationNetCore.WebApi.Abstractions
{
    public interface ICertificateService
    {
        X509Certificate2 getCertificate();
    }
}
