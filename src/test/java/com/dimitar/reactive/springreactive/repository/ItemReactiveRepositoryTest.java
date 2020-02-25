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


    @Test
    public void findItemByDescription() {
        final Mono<Item> items = itemReactiveRepository.findByDescription("Bose Headphones").log("find by description");

        StepVerifier.create(items)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }


    @Test
    public void saveItem() {
        final Item item = Item.builder().id(null).description("Google Home Mini").price(30d).build();
        final Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("Saved item: "))
                .expectSubscription()
                .expectNextMatches(it -> it.getId() != null && it.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }


    @Test
    public void updateItem() {
        final double newPrice = 3555d;

        Mono<Item> flux = itemReactiveRepository.findByDescription("TV LG")
                .map( item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item))
       ;


        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextMatches( it -> it.getPrice().doubleValue() == newPrice)
                .verifyComplete();
    }


    @Test
    public void deleteItemById() {
        Mono<Void> mono = itemReactiveRepository.findById("ABC").log()
                .map(Item::getId)
                .flatMap( id -> {
                    return itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(mono)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("New Item list: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }


    @Test
    public void deleteItem() {
        Mono<Void> mono = itemReactiveRepository.findByDescription("TV LG")
                .flatMap( item ->
                    itemReactiveRepository.delete(item)
                );

        StepVerifier.create(mono.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("New Item list: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }



}
