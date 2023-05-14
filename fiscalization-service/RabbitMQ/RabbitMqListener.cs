using FiscalizationNetCore.WebApi.Abstractions;
using FiscalizationNetCore.WebApi.Services;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using RabbitMQ.Client.Exceptions;
using ServiceReference;
using System;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;

namespace FiscalizationNetCore.WebApi.RabbitMQ
{
    public class RabbitMqListener : BackgroundService
    {
        private ICertificateService _certificateService;
        private IServiceProvider _serviceProvider;
        private ConnectionFactory _factory;
        private IConnection _connection;
        private IModel _channel;
        private RabbitMqPublisher _mqPublisher;

        private bool _rabbitMQUnreachable = false;

        // initialize the connection, channel and queue 
        // inside the constructor to persist them 
        // for until the service (or the application) runs
        public RabbitMqListener(
            ConnectionFactory connectionFactory,
            IServiceProvider serviceProvider,
            ICertificateService certificateService)
        {
            _serviceProvider = serviceProvider;
            _certificateService = certificateService;

            _factory = connectionFactory;
            _factory.AutomaticRecoveryEnabled = true;

            // try to connect every 10 seconds
            while (true)
            {
                Console.WriteLine("Trying to connect to RabbitMQ Broker...");
                try
                {
                    _connection = _factory.CreateConnection();
                    _channel = _connection.CreateModel();
                    _mqPublisher = new RabbitMqPublisher(connectionFactory);
                    _rabbitMQUnreachable = false;
                    Console.WriteLine("Connected to RabbitMQ Broker\n");
                    break;
                }
                catch (BrokerUnreachableException) 
                {
                    _rabbitMQUnreachable = true;
                    Console.WriteLine("RabbitMQ Broker Unreachable!");
                    Thread.Sleep(10000);
                }
            }
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            // if rabbitMQ unreachable do nothing
            if(_rabbitMQUnreachable)
                return Task.CompletedTask;
                
            // when the service is stopping dispose
            if (stoppingToken.IsCancellationRequested)
            {
                _channel.Dispose();
                _connection.Dispose();
                return Task.CompletedTask;
            }

            // create a consumer that listens on the channel (queue)
            var consumer = new EventingBasicConsumer(_channel);

            // handle the Received event on the consumer when there's a new message
            consumer.Received += async (model, ea) =>
            {
                // read the message bytes
                var body = Encoding.UTF8.GetString(ea.Body.ToArray());

                // convert back to object
                FiscalizationModel obj = JsonSerializer.Deserialize<FiscalizationModel>(body);

                var invoice = obj.ToRacunType();

                await fiscalizeAsync(invoice, stoppingToken);
                
            };
            
            _channel.BasicConsume(queue: "fiscalization.queue.to", autoAck: true, consumer: consumer);
            
            return Task.CompletedTask;
        }

        private async Task fiscalizeAsync(RacunType invoice, CancellationToken stoppingToken)
        {
            using (IServiceScope scope = _serviceProvider.CreateScope())
            {
                FiskalizacijaPortTypeClient scopedFiskalizacijaPortTypeClient =
                    scope.ServiceProvider.GetRequiredService<FiskalizacijaPortTypeClient>();

                IFiscalizationService scopedFiscalizationService = new FiscalizationService(scopedFiskalizacijaPortTypeClient, _certificateService);
                var result = await scopedFiscalizationService.SendInvoiceAsync(invoice);
                result.Zki = invoice.ZastKod;

                Console.WriteLine($"Zki: {result.Zki}, Jir: {result.Jir}");

                FiscalizationResponseModel response = new() { InvoiceId=invoice.BrRac.BrOznRac };
                if (result.Greske != null)
                {
                    response.Success = false;
                    response.Message = String.Join("; ",
                        result.Greske.Select(x => $"{x.SifraGreske} - {x.PorukaGreske}").ToArray());
                }
                else
                {
                    response.Success = true;
                    response.Message = $"JIR:{result.Jir}##kraj##ZKI:{result.Zki}";
                }

                _mqPublisher.Publish(payload: response.ToString(),
                    exchange: "internal.exchange",
                    queue: "fiscalization.queue.result",
                    routingKey: "internal.fiscalization-result.routing-key");
            }
        }
    }
}
