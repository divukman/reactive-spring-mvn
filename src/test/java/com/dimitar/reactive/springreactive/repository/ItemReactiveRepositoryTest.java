package com.dimitar.reactive.springreactive.repository;

import com.dimitar.reactive.springreactive.document.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemsList = Arrays.asList(
            Item.builder().id(null).description("TV Samsung").price(4000d).build(),
            Item.builder().id(null).description("TV LG").price(2000d).build(),
            Item.builder().id(null).description("TV Philips").price(5000d).build(),
            Item.builder().id(null).description("TV Sharp").price(1500d).build()
            );

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemsList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext( item -> {
                    System.out.println("Saved to embedded MongoDB: " + item);
                })
                .blockLast(); // Wait until all previous operations are completed
    }

    @Test
    public void getAllItems() {
        final Flux<Item> itemFlux = itemReactiveRepository.findAll();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();

    }
}
