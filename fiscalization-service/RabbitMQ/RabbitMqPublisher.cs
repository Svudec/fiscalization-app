using RabbitMQ.Client;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Web;

namespace FiscalizationNetCore.WebApi.RabbitMQ
{
    public class RabbitMqPublisher
    {
        private readonly ConnectionFactory _connectionFactory;
        public RabbitMqPublisher(ConnectionFactory connectionFactory) 
        {
            _connectionFactory = connectionFactory;
        }

        public void Publish(string payload, string exchange, string queue, string routingKey)
        {
            var connection = _connectionFactory.CreateConnection();
            var channel = connection.CreateModel();

            string body = HttpUtility.JavaScriptStringEncode(payload.Replace("\n", " "));

            channel.BasicPublish(exchange: exchange,
                routingKey: routingKey,
                basicProperties: null,
                body: Encoding.UTF8.GetBytes($"\"{body}\""));

            channel.Close();
            connection.Close();
        }


    }
}
