package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

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

    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux =
                Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                    .flatMap(s -> {
                        return Flux.fromIterable(convertToList(s));
                    })
                    .log()
                ;

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }


    @Test
    public void transformUsingFlatMap_usingParallel() {
        Flux<String> stringFlux =
                Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                        .window(2)
                        .flatMap( (s) ->
                                    s.map(this::convertToList).subscribeOn( parallel() )
                                )
                .flatMap(s -> Flux.fromIterable(s))
                .log()
                ;

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallel_maintainOrder() {
        Flux<String> stringFlux =
                Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                        .window(2)
                        .flatMapSequential( s ->
                                s.map(this::convertToList).subscribeOn( parallel() )
                        )
                        .flatMap(s -> Flux.fromIterable(s))
                        .log()
                ;

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

}
