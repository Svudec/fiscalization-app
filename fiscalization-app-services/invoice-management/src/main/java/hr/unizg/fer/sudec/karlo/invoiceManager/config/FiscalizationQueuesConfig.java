package hr.unizg.fer.sudec.karlo.invoiceManager.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FiscalizationQueuesConfig {
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.to-fiscalization}")
    private String toFiscalizationQueue;

    @Value("${rabbitmq.queues.fiscalization-result}")
    private String fiscalizationResultQueue;

    @Value("${rabbitmq.routing-keys.internal-to-fiscalization}")
    private String toFiscalizationInternalRoutingKey;

    @Value("${rabbitmq.routing-keys.internal-fiscalization-result}")
    private String fiscalizationResultInternalRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue toFiscalizationQueue(){
        return new Queue(this.toFiscalizationQueue);
    }

    @Bean
    public Queue fiscalizationResultQueue(){
        return new Queue(this.fiscalizationResultQueue);
    }

    @Bean
    public Binding internalToFiscalizationBinding() {
        return BindingBuilder
                .bind(toFiscalizationQueue())
                .to(internalTopicExchange())
                .with(this.toFiscalizationInternalRoutingKey);
    }

    @Bean
    public Binding internalFiscalizationResultBinding() {
        return BindingBuilder
                .bind(fiscalizationResultQueue())
                .to(internalTopicExchange())
                .with(this.fiscalizationResultInternalRoutingKey);
    }
}
