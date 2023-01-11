using FiscalizationNetCore.WebApi.Abstractions;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using System;
using System.IO;
using System.Security.Cryptography.X509Certificates;

namespace FiscalizationNetCore.WebApi.Services
{
    public class CertificateService : ICertificateService
    {
        private readonly X509Certificate2 _certificate;
        public CertificateService(IConfiguration configuration) {
            string pathToCert = $"{configuration.GetValue<string>(WebHostDefaults.ContentRootKey)}{Path.DirectorySeparatorChar}cert{Path.DirectorySeparatorChar}FISKAL_1.p12";
            string pass = configuration["AppSettings:FiraCertTest:password"];
            _certificate = new X509Certificate2(pathToCert, pass);
        }
        public X509Certificate2 getCertificate()
        {
            return _certificate;
        }
    }
}
