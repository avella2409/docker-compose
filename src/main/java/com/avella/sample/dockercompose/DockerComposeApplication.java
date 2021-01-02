package com.avella.sample.dockercompose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@SpringBootApplication
public class DockerComposeApplication {

    @Bean
    RouterFunction<ServerResponse> route(PersonRepository personRepository) {
        return RouterFunctions.route()
                .GET("/person/city/{city}", serverRequest -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(personRepository.findAllByCity(serverRequest.pathVariable("city")), Person.class))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(DockerComposeApplication.class, args);
    }
}
