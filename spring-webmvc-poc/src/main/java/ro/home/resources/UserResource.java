package ro.home.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.home.model.User;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/users")
class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody User user) {
        LOGGER.info("Received new user with name: {}", user.getName());
        return user;
    }
}
