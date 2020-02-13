package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformText {

    final List<String> lstNames = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> namesFlux =
                Flux.fromIterable(lstNames)
                .map(String::toUpperCase)
                .log()
                ;

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length() {
        Flux<Integer> namesFlux =
                Flux.fromIterable(lstNames)
                        .map(String::length)
                        .log()
                ;

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length_repeat() {
        Flux<Integer> namesFlux =
                Flux.fromIterable(lstNames)
                        .map(String::length)
                        .repeat(1)
                        .log()
                ;

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5,4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Filter() {
        Flux<String> namesFlux =
                Flux.fromIterable(lstNames)
                        .filter(s -> s.length() > 4)
                        .map(String::toUpperCase)
                        .log()
                ;

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }
}
