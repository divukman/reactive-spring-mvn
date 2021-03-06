package com.dimitar.reactive.springreactive.handler;

import com.dimitar.reactive.springreactive.document.Item;
import com.dimitar.reactive.springreactive.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ItemsHandler {

    final ItemReactiveRepository itemReactiveRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItem(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");
        final Mono<Item> monoItem = itemReactiveRepository.findById(id);
        return monoItem.flatMap(
                item -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(item)).switchIfEmpty(notFound));
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        final Mono<Item> monoItem = serverRequest.bodyToMono(Item.class);
        return monoItem.flatMap(
                item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item), Item.class)
        );
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");
        final Mono<Void> monoItem = itemReactiveRepository.deleteById(id);
        return monoItem.flatMap(
                item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(monoItem, Void.class)
        );
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");
        final Mono<Item> monoItem = serverRequest.bodyToMono(Item.class);

        Mono<Item> updatedItem =  monoItem.flatMap( item -> {
             return itemReactiveRepository.findById(id)
                            .flatMap( currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());
                                return itemReactiveRepository.save(currentItem);
                            });
         });


         return updatedItem.flatMap(
                 item -> {
                     return ServerResponse
                             .ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromObject(item));

                 }
         ).switchIfEmpty(notFound);
    }

}
