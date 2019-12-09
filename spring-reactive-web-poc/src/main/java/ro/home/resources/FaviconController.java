package ro.home.resources;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import java.io.IOException;

import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;


@RestController
@RequestMapping(path = "/favicon.ico")
class FaviconController {

    @GetMapping(produces = IMAGE_PNG_VALUE)
    public Mono<DataBuffer> getFavIcon() {
        try {
            byte[] image = new ClassPathResource("favicon.ico").getInputStream().readAllBytes();
            return Mono.just(new DefaultDataBufferFactory().wrap(image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
