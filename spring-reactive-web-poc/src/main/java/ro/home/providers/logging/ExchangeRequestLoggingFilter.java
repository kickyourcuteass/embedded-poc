package ro.home.providers.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Order(2)
@RestControllerAdvice
class ExchangeRequestLoggingFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverExchange, WebFilterChain filterChain) {
        var exchangeDecorator = new CachingServerRequestWebExchangeDecorator(serverExchange);
        return filterChain.filter(exchangeDecorator)
                .doOnEach(e -> LoggingUtils.logRequestMetadata(LOGGER,  (CachingServerHttpRequestDecorator) exchangeDecorator.getRequest()));
    }
}
