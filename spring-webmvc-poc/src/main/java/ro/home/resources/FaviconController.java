package ro.home.resources;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;


@RestController
@RequestMapping(path = "/favicon.ico")
class FaviconController {

    @GetMapping(produces = IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getFavIcon() {
        try {
            return new ClassPathResource("favicon.ico").getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
