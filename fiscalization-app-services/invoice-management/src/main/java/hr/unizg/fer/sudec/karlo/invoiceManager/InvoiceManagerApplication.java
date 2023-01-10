package hr.unizg.fer.sudec.karlo.invoiceManager;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {
        "hr.unizg.fer.sudec.karlo.invoiceManager",
        "hr.unizg.fer.sudec.karlo.amqp"
})
public class InvoiceManagerApplication {
    public static void main(String[] args){
        SpringApplication.run(InvoiceManagerApplication.class, args);
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
