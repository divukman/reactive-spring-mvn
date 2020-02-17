package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static java.lang.Thread.*;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        final Flux<Long> infiniteFlux =  Flux.interval(Duration.ofMillis(200))
                .log(); // val from 0 -> ... 1, 2,3 ... every 200 millis

        infiniteFlux.subscribe(el -> System.out.println("Value is: " + el));

        sleep(5000);
    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        final Flux<Long> infiniteFlux =  Flux.interval(Duration.ofMillis(200))
                .take(3) //limits to 3 elements, making it a final flux
                .log(); // val from 0 -> ... 1, 2,3 ... every 200 millis

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap() {
        final Flux<Integer> infiniteFlux =  Flux.interval(Duration.ofMillis(200))
                .map(l -> Integer.valueOf(l.intValue()))
                .take(3) //limits to 3 elements, making it a final flux
                .log(); // val from 0 -> ... 1, 2,3 ... every 200 millis

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMapWithDelay() {
        final Flux<Integer> infiniteFlux =  Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> Integer.valueOf(l.intValue()))
                .take(3) //limits to 3 elements, making it a final flux
                .log(); // val from 0 -> ... 1, 2,3 ... every 200 millis

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
