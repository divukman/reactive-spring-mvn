package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        /**
         * Cold publisher -> Emits the values from the start.
         */
        final Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(2))
                .log();

        stringFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));

        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2: " + s));

        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        /**
         * Hot publisher -> Emits the values in real time.
         */
        final Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1))
                .log();

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect(); //makes it a hot publisher

        connectableFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));
        Thread.sleep(3000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2: " + s));
        Thread.sleep(4000);
    }
}
