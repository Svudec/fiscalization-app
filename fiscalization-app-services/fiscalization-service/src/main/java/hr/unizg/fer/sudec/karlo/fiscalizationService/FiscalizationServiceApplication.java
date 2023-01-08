package hr.unizg.fer.sudec.karlo.fiscalizationService;

import hr.unizg.fer.sudec.karlo.amqp.RabbitMqMessageProducer;
import hr.unizg.fer.sudec.karlo.fiscalizationService.config.FiscalizationConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
        scanBasePackages = {
                "hr.unizg.fer.sudec.karlo.fiscalizationService",
                "hr.unizg.fer.sudec.karlo.amqp"
        }
)
public class FiscalizationServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(FiscalizationServiceApplication.class, args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(RabbitMqMessageProducer producer, FiscalizationConfig fiscalizationConfig){
//        return args -> {
//            producer.publish(
//                    "foo",
//                    fiscalizationConfig.getInternalExchange(),
//                    fiscalizationConfig.getFiscalizationInternalRoutingKey()
//                    );
//        };
//    }
}
