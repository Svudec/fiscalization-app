using FiscalizationNetCore.WebApi.Abstractions;
using Microsoft.Extensions.Configuration;
using System;
using System.Security.Cryptography.X509Certificates;

namespace FiscalizationNetCore.WebApi.Services
{
    public class CertificateService : ICertificateService
    {
        private readonly X509Certificate2 _certificate;
        public CertificateService(IConfiguration configuration) {
            _certificate = GetCertificateFromBase64(configuration["AppSettings:FiraCertTest:certificate"],
                configuration["AppSettings:FiraCertTest:password"]);
        }
        public X509Certificate2 getCertificate()
        {
            return _certificate;
        }

        private static X509Certificate2 GetCertificateFromBase64(string base64Certificate, string certificatePassword)
        {
            var raw = Convert.FromBase64String(base64Certificate);
            var certificate = new X509Certificate2(raw, certificatePassword);
            return certificate;
        }
    }
}
