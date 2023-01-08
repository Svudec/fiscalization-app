package hr.unizg.fer.sudec.karlo.catalogManagement;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {
        "hr.unizg.fer.sudec.karlo.catalogManagement",
        "hr.unizg.fer.sudec.karlo.amqp"
})
public class CatalogManagementApplication {
    public static void main(String[] args){
        SpringApplication.run(CatalogManagementApplication.class, args);
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
