package com.dimitar.reactive.springreactive.repository;

import com.dimitar.reactive.springreactive.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
            Item.builder().id(null).description("TV Sharp").price(1500d).build(),
            Item.builder().id("ABC").description("Bose Headphones").price(1500d).build()
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
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    public void getItemByID() {
        final Mono<Item> item = itemReactiveRepository.findById("ABC");

        StepVerifier.create(item)
                .expectSubscription()
                .expectNextMatches( itemMatch -> itemMatch.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }
}
