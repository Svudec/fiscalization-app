package hr.unizg.fer.sudec.karlo.fiscalizationService.messageQueue;

import hr.unizg.fer.sudec.karlo.fiscalizationService.service.FiscalizationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class InvoicesConsumer {

    private final FiscalizationService fiscalizationService;

    @RabbitListener(queues = "${rabbitmq.queues.fiscalization}")
    public void consumer(String invoice){
        log.info("Consumed {} from queue", invoice);
        boolean result = fiscalizationService.fiscalizeInvoice(invoice);
        log.info("Invoice {} is fiscalized: {}", invoice, result);
    }
}
