package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    final List<String> lstNames = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void filterTest() {
        final Flux<String> namesFlux =
                Flux.fromIterable(lstNames)
                        .filter(s -> s.startsWith("a"))
                        .log();

        StepVerifier.create(namesFlux)
                .expectNext("adam")
                .expectNext("anna")
                .verifyComplete();
    }

    @Test
    public void filterTestLength() {
        final Flux<String> namesFlux =
                Flux.fromIterable(lstNames)
                        .filter(s -> s.length() > 4)
                        .log();

        StepVerifier.create(namesFlux)
                .expectNext("jenny")
                .verifyComplete();
    }
}
