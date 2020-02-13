package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {
        final Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        final Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        final Flux<String> mergedFlux = Flux.merge(stringFlux1, stringFlux2)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMergeWithDelay() {
        final Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        final Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        final Flux<String> mergedFlux = Flux.merge(stringFlux1, stringFlux2)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                //.expectNext("A", "B", "C", "D", "E", "F")
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat() {
        final Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        final Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        final Flux<String> mergedFlux = Flux.concat(stringFlux1, stringFlux2)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcatDelayed() {
        final Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        final Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        final Flux<String> mergedFlux = Flux.concat(stringFlux1, stringFlux2)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }


    @Test
    public void combineUsingZip() {
        final Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        final Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        final Flux<String> mergedFlux = Flux.zip(stringFlux1, stringFlux2, (t1, t2) -> t1.concat(t2))
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

}
