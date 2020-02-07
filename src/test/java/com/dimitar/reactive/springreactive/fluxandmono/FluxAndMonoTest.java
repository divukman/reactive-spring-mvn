package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    public void testFlux() {
        final Flux<String> strFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            //    .concatWith(Flux.error(new RuntimeException("test error")))
                .concatWith(Flux.just("After exception"))
                .log();

        strFlux.subscribe(System.out::println,
                e -> System.err.println("Exception is: " + e),
                () -> System.out.println("Completed..."));
    }

}
