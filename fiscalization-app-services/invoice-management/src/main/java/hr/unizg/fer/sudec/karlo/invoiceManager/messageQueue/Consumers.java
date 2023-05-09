package hr.unizg.fer.sudec.karlo.invoiceManager.messageQueue;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.FiscalizationResultModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service.InvoiceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
@RabbitListener(queues = "${rabbitmq.queues.fiscalization-result}")
public class Consumers {

    private final InvoiceService invoiceService;

    @RabbitHandler
    public void fiscalizationResultConsumer(String result){
        log.info("Consumed from fiscalization-result queue. Message: {}", result);
        //invoiceService.handleFiscalizationResult(fiscalizationResult);
    }
}
