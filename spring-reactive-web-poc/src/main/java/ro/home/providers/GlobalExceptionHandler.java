package ro.home.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import ro.home.model.InformationMessage;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ro.home.model.enums.InformationMessageType.ERROR;

@RestControllerAdvice
@Order(-10)
class GlobalExceptionHandler implements WebExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable e) {
        LOGGER.error(e.getMessage(), e);

        ServerHttpResponse response = serverWebExchange.getResponse();
        response.getHeaders().setContentType(APPLICATION_JSON);
        response.setStatusCode(INTERNAL_SERVER_ERROR);

        DataBuffer dataBuffer = response.bufferFactory().wrap(getResponseBytes(new InformationMessage(ERROR, e.getMessage())));
        return response.writeWith(Mono.just(dataBuffer));
    }

    private byte[] getResponseBytes(InformationMessage message) {
        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return new byte[0];
        }
    }
}