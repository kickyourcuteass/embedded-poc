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

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Set;


import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String SPRING_BATCH_CONTEXT_PATH = "/spring-batch-demo";

    private static final Integer DEFAULT_SERVER_PORT = 9090;
    private static Integer HTTP_PORT = 9092;
    private static Undertow server;

    public static void main(String[] args) {

        try {
            LOGGER.info("Embedded Undertow server is starting.");
            //String keyStorePassword = ENCRYPTOR.decrypt(KEY_STORE_PASSWORD);
            //SSLContext sslContext = createSSLContext(loadKeyStore(KEY_STORE_PATH, keyStorePassword), keyStorePassword, null);
            if (nonNull(HTTP_PORT)) {
                LOGGER.info("HTTP_PORT is a valid integer with value : {}", HTTP_PORT);
            } else {
                LOGGER.info("HTTP_PORT has not been specified at startup... using default value : {}", DEFAULT_SERVER_PORT);
                HTTP_PORT = DEFAULT_SERVER_PORT;
            }
            // Deploy app into container and get deployment manager.
            // Add Handlers and start server
            PathHandler paths = Handlers.path()
                    .addPrefixPath(SPRING_BATCH_CONTEXT_PATH, initSpringBatchDemoDeployment().start());

            server = Undertow.builder()
                    .addHttpListener(HTTP_PORT, "0.0.0.0")
                    //.addHttpsListener(HTTPS_PORT, "0.0.0.0", sslContext)
                    .setHandler(paths)
                    .build();

            server.start();

            LOGGER.info("Undertow server started on HTTP  port : {}", HTTP_PORT);
            //LOGGER.info("Undertow server started on HTTPS port : {}", HTTPS_PORT);
            LOGGER.info("Hit enter to stop it...");
            // This is a bypass for starting in nohup mode under UNIX in case when System.in.read() throws an IOException
            try {
                System.in.read();
                stop();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }
    }


    private static void stop() {
        server.stop();
        LOGGER.info("Server stopped on user request.");
        System.exit(0);
    }


    private static DeploymentManager initSpringBatchDemoDeployment() {
        //addClassPath(".");
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(Servlets.deployment()
                .setDeploymentName("spring-batch-demo")
                .setClassLoader(Main.class.getClassLoader())
                .setContextPath(SPRING_BATCH_CONTEXT_PATH)
                .setDefaultEncoding("UTF-8")
                .setUrlEncoding("UTF-8")
                .addServletContainerInitalizer(new ServletContainerInitializerInfo(
                        SpringServletContainerInitializer.class,
                        new ImmediateInstanceFactory<>(new SpringServletContainerInitializer()),
                        Set.of(Bootstrap.class)
                )));
        manager.deploy();
        return manager;
    }

    private static SSLContext createSSLContext(final KeyStore keyStore, String keyStorePassword, final KeyStore trustStore)
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {

        KeyManager[] keyManagers;
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
        keyManagers = keyManagerFactory.getKeyManagers();

        TrustManager[] trustManagers;
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private static KeyStore loadKeyStore(String keyStorePath, String keyStorePassWord)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {

        final InputStream stream = new FileInputStream(keyStorePath);
        if (isNull(stream)) {
            throw new RuntimeException("Could not load keystore");
        }
        try (InputStream is = stream) {
            KeyStore loadedKeystore = KeyStore.getInstance("JKS");
            loadedKeystore.load(is, keyStorePassWord.toCharArray());
            is.close();
            return loadedKeystore;
        }
    }

    private static void addClassPath(String s) throws Exception {
        File f = new File(s);
        URI u = f.toURI();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, u.toURL());
    }
}
