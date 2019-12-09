package ro.home.providers.logging;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

class CachingServerResponseWebExchangeDecorator extends ServerWebExchangeDecorator {

    private final ServerHttpResponseDecorator responseDecorator;

    CachingServerResponseWebExchangeDecorator(ServerWebExchange delegate) {
        super(delegate);
        this.responseDecorator = new CachingServerHttpResponseDecorator(delegate.getResponse());
    }

    @Override
    public ServerHttpResponse getResponse() {
        return responseDecorator;
    }

}