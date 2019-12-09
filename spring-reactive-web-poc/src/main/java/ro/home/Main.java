package ro.home;

import io.undertow.Undertow;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import ro.home.config.AppConfig;
import ro.home.undertow.security.MapIdentityManager;
import ro.home.undertow.security.SecurityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    private static Integer HTTP_PORT = 9095;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        LOGGER.info("Embedded Undertow server is starting.");

        /*
        IdentityManager im = new MapIdentityManager(
                Map.of("user", "password".toCharArray(), "admin", "password".toCharArray()),
                Map.of("user", Set.of("USER"), "admin", Set.of("ADMIN")));

         */

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Undertow server = Undertow.builder()
                .addHttpListener(HTTP_PORT, "0.0.0.0")
                //.addHttpsListener(HTTPS_PORT, "0.0.0.0", sslContext)
                .setHandler(
                        new UndertowHttpHandlerAdapter(
                                WebHttpHandlerBuilder.webHandler(new DispatcherHandler(context))
                                        .applicationContext(context)
                                        .build()
                        )
                        /*SecurityUtils.addSecurity(
                        new UndertowHttpHandlerAdapter(
                                WebHttpHandlerBuilder.webHandler(new DispatcherHandler(context))
                                        .applicationContext(context)
                                        .build()
                        ),
                        "ADMIN",
                        List.of(new BasicAuthenticationMechanism("test")),
                        im */
                )
                .build();
        server.start();

        LOGGER.info("Undertow server started on HTTP  port : {}", HTTP_PORT);
        //LOGGER.info("Undertow server started on HTTPS port : {}", HTTPS_PORT);
        LOGGER.info("Hit enter to stop it...");

        System.in.read();
        ((ConfigurableApplicationContext) context).close();
        server.stop();
    }
}
