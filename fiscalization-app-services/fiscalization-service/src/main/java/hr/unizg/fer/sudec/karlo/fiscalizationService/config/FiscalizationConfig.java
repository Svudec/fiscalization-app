package hr.unizg.fer.sudec.karlo.fiscalizationService.config;

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
public class FiscalizationConfig {

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.fiscalization}")
    private String fiscalizationQueue;

    @Value("${rabbitmq.routing-keys.internal-fiscalization}")
    private String fiscalizationInternalRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue fiscalizationQueue(){
        return new Queue(this.fiscalizationQueue);
    }

    @Bean
    public Binding internalFiscalizationBinding() {
        return BindingBuilder
                .bind(fiscalizationQueue())
                .to(internalTopicExchange())
                .with(this.fiscalizationInternalRoutingKey);
    }
}
