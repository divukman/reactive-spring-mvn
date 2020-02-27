package com.dimitar.reactive.springreactive.bootstrap;

import com.dimitar.reactive.springreactive.document.Item;
import com.dimitar.reactive.springreactive.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Just a temp component for bootstrapping some data into the databaase.
 */
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository itemReactiveRepository;

/*    @Autowired
    public DataInitializer(final ItemReactiveRepository itemReactiveRepository) {
        this.itemReactiveRepository = itemReactiveRepository;
    }*/

    @Override
    public void run(String... args) {
        // itemReactiveRepository.deleteAll().block(); // uncomment to delete and re-insert new data
        if (itemReactiveRepository.count().block().longValue() == 0) {
            loadInitialData();
        }
    }


    public void loadInitialData() {
        final List<Item> lstItems = Arrays.asList(
                    Item.builder().id(null).description("LG TV").price(3000d).build(),
                    Item.builder().id(null).description("Samsung TV").price(3500d).build(),
                    Item.builder().id(null).description("Apple Watch").price(5000d).build(),
                    Item.builder().id("ABC").description("Sennheiser Headphones").price(2000d).build()
                );

        itemReactiveRepository.saveAll(lstItems)
                .subscribe(item -> System.out.println("Bootstrapped item: " + item));
    }
}
