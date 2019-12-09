package ro.home.providers.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Order(1)
@RestControllerAdvice
class ExchangeResponseLoggingFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeResponseLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverExchange, WebFilterChain filterChain) {
        var exchangeDecorator = new CachingServerResponseWebExchangeDecorator(serverExchange);
        return filterChain.filter(exchangeDecorator)
                .doOnEach(e -> LoggingUtils.logResponseMetadata(LOGGER, (CachingServerHttpResponseDecorator) exchangeDecorator.getResponse()));
    }
}
