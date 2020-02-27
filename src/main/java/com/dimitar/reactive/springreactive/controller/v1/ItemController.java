package com.dimitar.reactive.springreactive.controller.v1;

import com.dimitar.reactive.springreactive.constants.ItemConstants;
import com.dimitar.reactive.springreactive.document.Item;
import com.dimitar.reactive.springreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
}
