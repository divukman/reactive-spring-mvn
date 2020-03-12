package com.dimitar.reactive.springreactive.handler;

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
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@SuppressWarnings("Duplicates")
@ActiveProfiles("test")
public class ItemHandlerTest {
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
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllItemsV2() {
        webTestClient
                .get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
        .consumeWith(response -> {
            List<Item> lstItems = response.getResponseBody();
            lstItems.forEach(item -> assertTrue(item.getId() != null));
        });
    }

    @Test
    public void getAllItemsV3() {
        Flux<Item> fluxItem =  webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(fluxItem.log("value from network: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",2000d);
    }
}
