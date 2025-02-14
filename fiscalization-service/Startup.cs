using System;
using System.Security.Cryptography.X509Certificates;
using System.ServiceModel;
using System.ServiceModel.Security;
using FiscalizationNetCore.WebApi.Abstractions;
using FiscalizationNetCore.WebApi.RabbitMQ;
using FiscalizationNetCore.WebApi.Services;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using RabbitMQ.Client;
using ServiceReference;

namespace FiscalizationNetCore.WebApi
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddControllers();

            services.AddTransient<IFiscalizationService, FiscalizationService>();
            services.AddScoped(provider =>
            {
                var binding = new BasicHttpsBinding(BasicHttpsSecurityMode.Transport);
                binding.Security.Transport.ClientCredentialType = HttpClientCredentialType.Certificate;
                
                var endpoint = new EndpointAddress(new Uri(Configuration["AppSettings:CisUrl"]));
                
                Console.WriteLine(endpoint.Uri);
                
                var client = new FiskalizacijaPortTypeClient(binding, endpoint);
                client.ClientCredentials.ServiceCertificate.SslCertificateAuthentication =
                    new X509ServiceCertificateAuthentication
                    {
                        CertificateValidationMode = X509CertificateValidationMode.None,
                        RevocationMode = X509RevocationMode.NoCheck
                    };
                
                return client;
            });
            services.AddSingleton<ICertificateService>(x => new CertificateService(Configuration));
            //RabbitMq connection
            services.AddSingleton(serviceProvider =>
        {
            var uri = new Uri(Configuration["AppSettings:RabbitMqURL"]);
            return new ConnectionFactory
            {
                Uri = uri
            };
        });
            services.AddHostedService<RabbitMqListener>();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseHttpsRedirection();

            app.UseRouting();

            app.UseAuthorization();

            app.UseEndpoints(endpoints => { endpoints.MapControllers(); });
        }
    }
}