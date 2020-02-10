package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> lstNames = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingIterable() {
        final Flux<String> namesFlux = Flux.fromIterable(lstNames).log();
        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        final String[] names = new String[] {"adam", "anna", "jack", "jenny"};
        final Flux<String> namesFlux = Flux.fromArray(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }


    @Test
    public void fluxusingStream() {
        Flux<String> namesFlux = Flux.fromStream(lstNames.stream()).log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        final Mono<String> mono = Mono.justOrEmpty(null); // Mono.empty();

        StepVerifier.create(mono)
                // NO data to emmit, just verify complete
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        final Supplier<String> stringSupplier = () -> "adam";

        final Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono)
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        final Flux<Integer> intFlux = Flux.range(1, 5);

        StepVerifier.create(intFlux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
