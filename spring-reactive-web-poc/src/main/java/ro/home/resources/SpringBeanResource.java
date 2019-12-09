package ro.home.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebFilter;

@RestController
@RequestMapping("/springBeans")
class SpringBeanResource {

    @Autowired
    private ApplicationContext context;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public MultiValueMap<String, String> getSpringBeans() {
        MultiValueMap<String, String> springBeans = new LinkedMultiValueMap<>();
        for (String bean : context.getBeanDefinitionNames()) {
            springBeans.add(context.getType(bean).getName(), bean);
        }
        return springBeans;
    }

    @GetMapping(path = "/web-filters", produces = MediaType.APPLICATION_JSON_VALUE)
    public MultiValueMap<String, String> getWebFiltersSpringBeans() {
        MultiValueMap<String, String> springBeans = new LinkedMultiValueMap<>();
        for (String bean : context.getBeanNamesForType(WebFilter.class)) {
            springBeans.add(context.getType(bean).getName(), bean);
        }
        return springBeans;
    }
}
