using RabbitMQ.Client;
using System.Text.Json;

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

            channel.BasicPublish(exchange: exchange,
                routingKey: routingKey,
                basicProperties: null,
                body: JsonSerializer.SerializeToUtf8Bytes(payload));

            channel.Close();
            connection.Close();
        }


    }
}
