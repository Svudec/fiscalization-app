package hr.unizg.fer.sudec.karlo.amqp.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FiscalizationQueuesConfig {
    public static final String internalExchange = "internal.exchange";

    public static final String toFiscalizationQueue = "fiscalization.queue.to";

    public static final String fiscalizationResultQueue = "fiscalization.queue.result";

    public static final String toFiscalizationInternalRoutingKey = "internal.to-fiscalization.routing-key";

    public static final String fiscalizationResultInternalRoutingKey = "internal.fiscalization-result.routing-key";

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
