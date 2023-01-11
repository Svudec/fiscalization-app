package hr.unizg.fer.sudec.karlo.invoiceManager.messageQueue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Getter
public class QueueParamsService {
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
}
