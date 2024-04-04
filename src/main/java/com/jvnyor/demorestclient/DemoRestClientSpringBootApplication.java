package com.jvnyor.demorestclient;

import com.jvnyor.demorestclient.services.CrudService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoRestClientSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRestClientSpringBootApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(CrudService crudService) {
        return args -> {
//            var smith = new CatRequestDTO("Smith", "Black", 6.0);
//            var barry = new CatRequestDTO("Barry", "White", 7.5);
//            var smithResponse = crudService.createCat(smith);
//            System.out.println(smithResponse);
//            System.out.println(crudService.createCat(barry));
//            final var id = smithResponse._id();
//            System.out.println(crudService.getCat(id));
//            System.out.println(crudService.getCat("60f1b3b3b3b3b3b3b3b3b3b3"));
//            smith = smith.withWeight(7.0);
//            crudService.updateCat(id, smith);
//            System.out.println(crudService.getCat(id));
//            crudService.deleteCat(id);
//            System.out.println(crudService.listCats());
        };
    }
}