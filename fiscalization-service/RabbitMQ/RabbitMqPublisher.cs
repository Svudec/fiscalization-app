using RabbitMQ.Client;
using System;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace FiscalizationNetCore.WebApi.RabbitMQ
{
    public class RabbitMqPublisher
    {
        private readonly ConnectionFactory _connectionFactory;
        public RabbitMqPublisher(ConnectionFactory connectionFactory) 
        {
            _connectionFactory = connectionFactory;
        }

        public void Publish(object payload, string exchange, string queue, string routingKey)
        {
            var connection = _connectionFactory.CreateConnection();
            var channel = connection.CreateModel();

            channel.ExchangeDeclare(exchange: exchange, type: "topic", durable: true, autoDelete: false);
            channel.QueueDeclare(queue: queue, durable: true, autoDelete: false);
            channel.ExchangeBind(source: queue, destination: exchange, routingKey: routingKey);

            channel.BasicPublish(exchange: exchange,
                routingKey: routingKey,
                basicProperties: null,
                body: JsonSerializer.SerializeToUtf8Bytes(payload));

            channel.Close();
            connection.Close();
        }


    }
}
