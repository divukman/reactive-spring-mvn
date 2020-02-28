package com.dimitar.reactive.springreactive.controller.v1;

import com.dimitar.reactive.springreactive.constants.ItemConstants;
import com.dimitar.reactive.springreactive.document.Item;
import com.dimitar.reactive.springreactive.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@SuppressWarnings("Duplicates")
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient; // web test client for non blocking endpoint

    @Autowired
    ItemReactiveRepository itemReactiveRepository;


    @BeforeEach
    public void setUp() {
        final List<Item> lstItems = Arrays.asList(
                Item.builder().id(null).description("LG TV").price(3000d).build(),
                Item.builder().id(null).description("Samsung TV").price(3500d).build(),
                Item.builder().id(null).description("Apple Watch").price(5000d).build(),
                Item.builder().id("ABC").description("Sennheiser Headphones").price(2000d).build()
        );

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(lstItems))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted: " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllItemsApproach2() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith( response -> response.getResponseBody().forEach(item -> assertTrue(item.getId() != null)));
    }

    @Test
    public void getAllItemsApproach3() {
        final Flux<Item> fluxItems = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(fluxItems.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",2000d);
    }


    @Test
    public void getOneItemInvalidItem() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABCDEF")
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    public void createItem() {
        final Item item = Item.builder().id(null).description("Iphone X").price(5000d).build();
        webTestClient
                .post()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X")
                .jsonPath("$.price").isEqualTo(5000d);
    }


    @Test
    public void deleteItem() {
        webTestClient
                .delete()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem() {
        final double newPrice = 6000d;
        final Item item = Item.builder().id(null).description("Iphone X").price(newPrice).build();

        webTestClient
                .put()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X")
                .jsonPath("$.price").isEqualTo(newPrice);
    }

    @Test
    public void updateItemInvalidId() {
        final double newPrice = 6000d;
        final Item item = Item.builder().id(null).description("Iphone X").price(newPrice).build();

        webTestClient
                .put()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABCDEF")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }


}
