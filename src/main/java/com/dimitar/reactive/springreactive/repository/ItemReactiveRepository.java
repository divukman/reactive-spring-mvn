package com.dimitar.reactive.springreactive.repository;

import com.dimitar.reactive.springreactive.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
}
