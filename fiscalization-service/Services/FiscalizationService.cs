using System;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Xml;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Serialization;
using FiscalizationNetCore.WebApi.Abstractions;
using ServiceReference;

namespace FiscalizationNetCore.WebApi.Services
{
    public class FiscalizationService : IFiscalizationService
    {
        private readonly FiskalizacijaPortTypeClient _fiskalizacijaPortTypeClient;
        private readonly X509Certificate2 _certificate;

        public FiscalizationService(FiskalizacijaPortTypeClient fiskalizacijaPortTypeClient, ICertificateService certificateService)
        {
            _fiskalizacijaPortTypeClient = fiskalizacijaPortTypeClient;
            _certificate = certificateService.getCertificate();
        }


        /// <summary>
        /// Represent request data for CIS service
        /// </summary>
        public interface ICisRequest
        {
            string Id { get; set; }
            SignatureType Signature { get; set; }
        }

        /// <summary>
        /// Represent response data from CIS service
        /// </summary>
        public interface ICisResponse
        {
            GreskaType[] Greske { get; set; }
            ICisRequest Request { get; set; }
        }

        public const string DATE_FORMAT_LONG = "dd.MM.yyyyTHH:mm:ss";

        /// <summary>
        /// Get default request header
        /// </summary>
        /// <returns></returns>
        public ZaglavljeType GetRequestHeader()
        {
            return new ZaglavljeType
            {
                IdPoruke = Guid.NewGuid().ToString(),
                DatumVrijeme = DateTime.Now.ToString(DATE_FORMAT_LONG)
            };
        }

        /// <summary>
        /// Generate ZKI code
        /// </summary>
        /// <param name="invoice">Invoice to calculate and generate ZKI</param>
        public void GenerateZki(RacunType invoice)
        {
            var sb = new StringBuilder();
            sb.Append(invoice.Oib);
            sb.Append(invoice.DatVrijeme);
            sb.Append(invoice.BrRac.BrOznRac);
            sb.Append(invoice.BrRac.OznPosPr);
            sb.Append(invoice.BrRac.OznNapUr);
            sb.Append(invoice.IznosUkupno);

            invoice.ZastKod = SignAndHashMD5(sb.ToString());
        }

        /// <summary>
        /// Sign and hash with MD5 algorithm
        /// </summary>
        /// <param name="value">String to encrypt</param>
        /// <returns>Encrypted string</returns>
        public string SignAndHashMD5(string value)
        {
            if (value == null) throw new ArgumentNullException("value");

            // Sign data
            byte[] b = Encoding.ASCII.GetBytes(value);

            var provider = _certificate.GetRSAPrivateKey();
            var signData = provider.SignData(b, HashAlgorithmName.SHA1, RSASignaturePadding.Pkcs1);

            // Compute hash
            MD5 md5 = MD5.Create();
            byte[] hash = md5.ComputeHash(signData);
            var result = new string(hash.SelectMany(x => x.ToString("x2")).ToArray());

            return result;
        }

        /// <summary>
        /// Serialize request data
        /// </summary>
        /// <param name="request">Request to serialize</param>
        /// <returns></returns>
        public string Serialize(ICisRequest request)
        {
            if (request == null) throw new ArgumentNullException("request");

            // Fix empty arrays to null
            if (request is RacunZahtjev)
            {
                var rz = (RacunZahtjev)request;

                if (rz.Racun == null) throw new ArgumentNullException("request.Racun");

                var r = rz.Racun;
                Action<Array, Action> fixArray = (x, y) =>
                {
                    var isEmpty = x != null && !x.OfType<object>().Any(x1 => x1 != null);
                    if (isEmpty)
                        y();
                };
                fixArray(r.Naknade, () => r.Naknade = null);
                fixArray(r.OstaliPor, () => r.OstaliPor = null);
                fixArray(r.Pdv, () => r.Pdv = null);
                fixArray(r.Pnp, () => r.Pnp = null);
            }

            using (var ms = new MemoryStream())
            {
                // Set namespace to root element
                var root = new XmlRootAttribute
                    { Namespace = "http://www.apis-it.hr/fin/2012/types/f73", IsNullable = false };
                var ser = new XmlSerializer(request.GetType(), root);
                ser.Serialize(ms, request);

                // need to remove first char, otherwise returns some weird unicode "zero width no-break space"
                return Encoding.UTF8.GetString(ms.ToArray());
                // return Encoding.UTF8.GetString(ms.ToArray()).Remove(0, 1);
            }
        }

        /// <summary>
        /// Sign request
        /// </summary>
        /// <param name="request">Request to sign</param>
        public void Sign(ICisRequest request)
        {
            if (request == null) throw new ArgumentNullException("request");

            if (request.Signature != null)
                // Already signed
                return;

            // Check if ZKI is generated
            var invoiceRequest = request as RacunZahtjev;
            if (invoiceRequest != null && invoiceRequest.Racun.ZastKod == null)
                GenerateZki(invoiceRequest.Racun);

            request.Id = request.GetType().Name;

            SignedXml xml = null;
            var ser = Serialize(request);
            var doc = new XmlDocument();
            doc.LoadXml(ser);

            xml = new SignedXml(doc);
            xml.SigningKey = _certificate.GetRSAPrivateKey();
            xml.SignedInfo.CanonicalizationMethod = SignedXml.XmlDsigExcC14NTransformUrl;

            var keyInfo = new KeyInfo();
            var keyInfoData = new KeyInfoX509Data();
            keyInfoData.AddCertificate(_certificate);
            keyInfoData.AddIssuerSerial(_certificate.Issuer, _certificate.GetSerialNumberString());
            keyInfo.AddClause(keyInfoData);
            xml.KeyInfo = keyInfo;

            var transforms = new Transform[]
            {
                new XmlDsigEnvelopedSignatureTransform(false),
                new XmlDsigExcC14NTransform(false)
            };

            Reference reference = new Reference("#" + request.Id);
            foreach (var x in transforms)
                reference.AddTransform(x);
            xml.AddReference(reference);
            xml.ComputeSignature();

            var s = xml.Signature;

            dynamic x509IssuerSerial = keyInfoData.IssuerSerials[0];
            //var certSerial = (X509IssuerSerialType)keyInfoData.IssuerSerials[0];
            request.Signature = new SignatureType
            {
                SignedInfo = new SignedInfoType
                {
                    CanonicalizationMethod = new CanonicalizationMethodType
                        { Algorithm = s.SignedInfo.CanonicalizationMethod },
                    SignatureMethod = new SignatureMethodType { Algorithm = s.SignedInfo.SignatureMethod },
                    Reference =
                        (from x in s.SignedInfo.References.OfType<Reference>()
                            select new ReferenceType
                            {
                                URI = x.Uri,
                                Transforms =
                                    (from t in transforms
                                        select new TransformType { Algorithm = t.Algorithm }).ToArray(),
                                DigestMethod = new DigestMethodType { Algorithm = x.DigestMethod },
                                DigestValue = x.DigestValue
                            }).ToArray()
                },
                SignatureValue = new SignatureValueType { Value = s.SignatureValue },
                KeyInfo = new KeyInfoType
                {
                    ItemsElementName = new[] { ItemsChoiceType2.X509Data },
                    Items = new[]
                    {
                        new X509DataType
                        {
                            ItemsElementName = new[]
                            {
                                ItemsChoiceType.X509IssuerSerial,
                                ItemsChoiceType.X509Certificate
                            },
                            Items = new object[]
                            {
                                new X509IssuerSerialType
                                {
                                    X509IssuerName = x509IssuerSerial.IssuerName,
                                    X509SerialNumber = x509IssuerSerial.SerialNumber
                                },
                                _certificate.RawData
                            }
                        }
                    }
                }
            };
        }

        void ThrowOnResponseErrors(ICisResponse response)
        {
            var errors = response?.Greske ?? new GreskaType[] { };

            // Special case for CheckInvoice service method
            // - returns error for success check WTF!!!!
            if (response is ProvjeraOdgovor)
            {
                // Remove "valid error" from response
                errors = errors.Where(x => x.SifraGreske != "v100").ToArray();
                response.Greske = errors;
            }

            if (errors.Any())
            {
                var strErrors = errors.Select(x => $"({x.SifraGreske}) {x.PorukaGreske}");
                var exMsg = string.Join("\n", strErrors);

                throw new Exception($"Fiscalization errors: {exMsg}");
            }
        }

        /// <summary>
        /// Send invoice async
        /// </summary>
        /// <param name="invoice">Invoice to send</param>
        public Task<RacunOdgovor> SendInvoiceAsync(RacunType invoice)
        {
            if (invoice == null) throw new ArgumentNullException("invoice");

            var request = new RacunZahtjev
            {
                Racun = invoice,
                Zaglavlje = GetRequestHeader()
            };

            return SendInvoiceRequestAsync(request);
        }


        /// <summary>
        /// Send invoice request async
        /// </summary>
        /// <param name="request">Request to send</param>
        public Task<RacunOdgovor> SendInvoiceRequestAsync(RacunZahtjev request)
        {
            if (request == null) throw new ArgumentNullException("request");
            if (request.Racun == null) throw new ArgumentNullException("request.Racun");

            return SignAndSendRequestAsync<RacunZahtjev, RacunOdgovor>(request, x => x.RacuniAsync);
        }


        /// <summary>
        /// Send request async
        /// </summary>
        /// <typeparam name="TRequest">Type of service method argument</typeparam>
        /// <typeparam name="TResponse">Type of service method result</typeparam>
        /// <param name="request">Request to send</param>
        /// <param name="serviceMethod">Function to provide service method</param>
        /// <returns>Service response object</returns>
        public async Task<TResponse> SignAndSendRequestAsync<TRequest, TResponse>(TRequest request,
            Func<FiskalizacijaPortTypeClient, Func<TRequest, Task<TResponse>>> serviceMethod)
            where TRequest : ICisRequest
            where TResponse : ICisResponse
        {
            if (request == null) throw new ArgumentNullException("request");
            if (serviceMethod == null) throw new ArgumentNullException("serviceMethod");

            // Sign request
            Sign(request);

            // Insert certificate into client
            _fiskalizacijaPortTypeClient.ClientCredentials.ClientCertificate.Certificate = _certificate;

            // Send request to fiscalization service
            var method = serviceMethod(_fiskalizacijaPortTypeClient);
            var result = await method(request);

            // Add reference to request object
            result.Request = request;

            // ThrowOnResponseErrors(result)

            return result;
        }
    }
}