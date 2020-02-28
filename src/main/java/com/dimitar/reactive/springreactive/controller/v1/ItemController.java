package com.dimitar.reactive.springreactive.controller.v1;

import com.dimitar.reactive.springreactive.constants.ItemConstants;
import com.dimitar.reactive.springreactive.document.Item;
import com.dimitar.reactive.springreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

    //@todo: create a service
    @Autowired
    ItemReactiveRepository itemReactiveRepository;


    @GetMapping(ItemConstants.ITEM_END_POINT_V1)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }


    @GetMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable final String id) {
        return itemReactiveRepository.findById(id)
                .map( item -> new ResponseEntity<>(item, HttpStatus.OK) )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ItemConstants.ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody final Item item) {
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteItem(@PathVariable final String id) {
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable final String id, @RequestBody final Item item) {
        return itemReactiveRepository.findById(id)
                .flatMap( it -> {
                    it.setPrice(item.getPrice());
                    it.setDescription(item.getDescription());
                    return itemReactiveRepository.save(it); //@todo we could just save the item!?
                })
                .map(updatedItem -> new ResponseEntity<Item>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty( new ResponseEntity<Item>(HttpStatus.NOT_FOUND));
    }

}
