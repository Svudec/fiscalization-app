package hr.unizg.fer.sudec.karlo.invoiceManager.messageQueue;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service.InvoiceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class Consumers {

    private final InvoiceService invoiceService;

    @RabbitListener(queues = "${rabbitmq.queues.fiscalization-result}")
    public void fiscalizationResultConsumer(String fiscalizationResult){
        log.info("Consumed {} from fiscalization-result queue", fiscalizationResult);
        invoiceService.handleFiscalizationResult(fiscalizationResult);
    }
}
