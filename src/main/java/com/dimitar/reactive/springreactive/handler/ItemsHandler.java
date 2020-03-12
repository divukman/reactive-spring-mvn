package com.dimitar.reactive.springreactive.handler;

import com.dimitar.reactive.springreactive.document.Item;
import com.dimitar.reactive.springreactive.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

}
