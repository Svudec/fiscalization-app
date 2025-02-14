package hr.unizg.fer.sudec.karlo.catalogManagement;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableFeignClients
@PropertySources({@PropertySource("classpath:clients-${spring.profiles.active}.properties")})
public class CatalogManagementApplication {
    public static void main(String[] args){
        SpringApplication.run(CatalogManagementApplication.class, args);
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
