package ro.home;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.SpringServletContainerInitializer;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Set;

public class Main {

    private static Integer HTTP_PORT = 9096;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws ServletException, IOException {
        DeploymentManager deploymentManager = initSpringWebmvcPocDeployment();

        PathHandler paths = Handlers.path()
                .addPrefixPath("/", deploymentManager.start());
        Undertow server = Undertow.builder()
                .addHttpListener(HTTP_PORT, "0.0.0.0")
                //.addHttpsListener(HTTPS_PORT, "0.0.0.0", sslContext)
                .setHandler(paths)
                .build();

        server.start();
        LOGGER.info("Undertow server started on HTTP  port : {}", HTTP_PORT);
        //LOGGER.info("Undertow server started on HTTPS port : {}", HTTPS_PORT);
        LOGGER.info("Hit enter to stop it...");

        System.in.read();
        deploymentManager.undeploy();
        server.stop();
    }

    private static DeploymentManager initSpringWebmvcPocDeployment() {
        //addClassPath(".");
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(Servlets.deployment()
                .setDeploymentName("spring-webmvc-poc")
                .setClassLoader(Main.class.getClassLoader())
                .setContextPath("/")
                .setDefaultEncoding("UTF-8")
                .setUrlEncoding("UTF-8")
                .addServletContainerInitializers(new ServletContainerInitializerInfo(
                        SpringServletContainerInitializer.class,
                        new ImmediateInstanceFactory<>(new SpringServletContainerInitializer()),
                        Set.of(Bootstrap.class)
                )));
        manager.deploy();
        return manager;
    }
}
