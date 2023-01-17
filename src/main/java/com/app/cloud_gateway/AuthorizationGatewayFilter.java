package com.app.cloud_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class AuthorizationGatewayFilter extends AbstractGatewayFilterFactory<AuthorizationGatewayFilter.Config> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    public AuthorizationGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> webClientBuilder
                .baseUrl("https://login/login")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/validatedCustomerId")
                        .queryParam("customerId", exchange.getRequest().getQueryParams().getFirst("customerId"))
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(isCustomerValid -> {
                    if (isCustomerValid) {
                        return chain.filter(exchange);
                    } else {
                        return onError(exchange, "Invalid Authorization", HttpStatus.UNAUTHORIZED);
                    }
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus unauthorized) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.setStatusCode(unauthorized);
        byte[] bytes = message.getBytes(UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return serverHttpResponse.writeWith(Flux.just(buffer));
    }

    public static class Config {
    }

}
