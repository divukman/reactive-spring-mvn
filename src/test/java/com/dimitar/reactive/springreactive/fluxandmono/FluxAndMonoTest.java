package com.dimitar.reactive.springreactive.fluxandmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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


    @Test
    public void testFluxElementsWithoutError() {
        final Flux<String> strFlux =
                Flux
                        .just("Spring", "Spring Boot", "Reactive Spring")
                        .log();

        StepVerifier.create(strFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }


    @Test
    public void testFluxElementsWithError() {
        final Flux<String> strFlux =
                Flux
                        .just("Spring", "Spring Boot", "Reactive Spring")
                        .concatWith(Flux.error(new RuntimeException("test error")))
                        .log();

        StepVerifier.create(strFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
               // .expectError(RuntimeException.class)
                .expectErrorMessage("test error")
                .verify();
    }

    @Test
    public void testFluxElementsCountWithError() {
        final Flux<String> strFlux =
                Flux
                    .just("Spring", "Spring Boot", "Reactive Spring")
                    .concatWith(Flux.error(new RuntimeException("test error")))
                    .log();

        StepVerifier.create(strFlux)
                .expectNextCount(3)
                .expectErrorMessage("test error")
                .verify();
    }


    @Test
    public void testFluxElementsWithError_2() {
        final Flux<String> strFlux =
                Flux
                        .just("Spring", "Spring Boot", "Reactive Spring")
                        .concatWith(Flux.error(new RuntimeException("test error")))
                        .log();

        StepVerifier.create(strFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectErrorMessage("test error")
                .verify();
    }
}
