package hr.unizg.fer.sudec.karlo.invoiceManager;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(scanBasePackages = {
        "hr.unizg.fer.sudec.karlo.invoiceManager",
        "hr.unizg.fer.sudec.karlo.amqp"
})
@EnableFeignClients
@PropertySources({@PropertySource("classpath:clients-${spring.profiles.active}.properties")})
public class InvoiceManagerApplication {
    public static void main(String[] args){
        SpringApplication.run(InvoiceManagerApplication.class, args);
    }

}
