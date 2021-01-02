package com.avella.sample.dockercompose;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

interface PersonRepository extends ReactiveMongoRepository<Person, String> {
    Flux<Person> findAllByCity(String city);
}