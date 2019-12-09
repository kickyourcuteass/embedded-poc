package ro.home.providers.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Order(1)
//@RestControllerAdvice
class ExchangeLoggingFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverExchange, WebFilterChain filterChain) {
        CachingServerWebExchangeDecorator exchangeDecorator = new CachingServerWebExchangeDecorator(serverExchange);
        return filterChain
                .filter(exchangeDecorator)
                .doOnEach(e -> {
                    LoggingUtils.logRequestMetadata(LOGGER, (CachingServerHttpRequestDecorator) exchangeDecorator.getRequest());
                    LoggingUtils.logResponseMetadata(LOGGER, (CachingServerHttpResponseDecorator) exchangeDecorator.getResponse());
                });
    }
}
