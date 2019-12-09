package ro.home.app.resources;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController(value = "/test")
class TestResource {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> testResource(){
        return Map.of("name", "andrei", "surname", "padureanu");
    }
}
