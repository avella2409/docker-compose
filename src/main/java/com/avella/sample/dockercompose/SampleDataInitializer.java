package com.avella.sample.dockercompose;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
class SampleDataInitializer {

    private final PersonRepository personRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {

        Flux<Person> saveAllPerson = personRepository.saveAll(Flux.just(
                new Person(null, "Anthony", "paris"),
                new Person(null, "Celia", "paris"),
                new Person(null, "Thomas", "lyon"),
                new Person(null, "Romain", "marseille")
        ));

        personRepository.deleteAll()
                .thenMany(saveAllPerson)
                .subscribe();
    }
}