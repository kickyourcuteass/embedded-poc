package ro.home;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.*;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.SpringServletContainerInitializer;
import ro.home.app.Bootstrap;
import ro.home.server.handler.AdministrationHandler;

import ro.home.server.properties.ServerProperties;

import ro.home.undertow.handler.AuthorizationHandler;
import ro.home.undertow.security.MapIdentityManager;
import ro.home.undertow.security.SecurityUtils;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static ro.home.server.properties.ServerProperties.DEFAULT_SERVER_PORT;

public class Main {

    private static final StandardPBEStringEncryptor ENCRYPTOR = new StandardPBEStringEncryptor();

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String CONTEXT_PATH = "/test-poc";
    private static Undertow server;
    private static DeploymentManager deploymentManager;
    private static IdentityManager identityManager;

    public static void main(String[] args) {
        try {
            ServerProperties.checkInputParameters();
            //ENCRYPTOR.setPassword(JASYPT_ENCRYPTION_KEY);
            System.setProperty("JASYPT_PASS", "**********");
            LOGGER.info("Embedded Undertow server is starting.");
            //String keyStorePassword = ENCRYPTOR.decrypt(KEY_STORE_PASSWORD);
            //SSLContext sslContext = createSSLContext(loadKeyStore(KEY_STORE_PATH, keyStorePassword), keyStorePassword, null);
            identityManager = initialiseIdentityManager();
            Integer HTTP_PORT;
            if (nonNull(System.getProperty("server.port"))) {
                try {
                    HTTP_PORT = Integer.parseInt(System.getProperty("server.port"));
                    LOGGER.info("server.port is a valid integer with value : {}", HTTP_PORT);
                } catch (NumberFormatException e) {
                    LOGGER.warn("server.port is not a valid integer... using default value : {}", DEFAULT_SERVER_PORT);
                    LOGGER.error(e.getMessage(), e);
                    HTTP_PORT = DEFAULT_SERVER_PORT;
                }
            } else {
                LOGGER.info("server.port has not been specified at startup... using default value : {}", DEFAULT_SERVER_PORT);
                HTTP_PORT = DEFAULT_SERVER_PORT;
            }

            ServletContainerInitializerInfo sciInfo = injectSpringWebAppInitializerIntoUndertowContainer();
            // Deploy app into container and get deployment manager.
            deploymentManager = initialiseDeploymentManager(sciInfo);

            // Add Handlers and start server
            PathHandler paths = Handlers.path()
                    .addPrefixPath(CONTEXT_PATH, deploymentManager.start())
                    .addPrefixPath("/server",
                            SecurityUtils.addSecurity(new AdministrationHandler(), "admin",
                                    asList(new BasicAuthenticationMechanism("POC Admin")),
                                    identityManager
                            ));

            server = Undertow.builder()
                    .addHttpListener(HTTP_PORT, "0.0.0.0")
                    //.addHttpsListener(HTTPS_PORT, "0.0.0.0", sslContext)
                    .setHandler(paths)
                    .build();

            server.start();
            LOGGER.info("Undertow server started on HTTP  port : {}", HTTP_PORT);
            //LOGGER.info("Undertow server started on HTTPS port : {}", HTTPS_PORT);
            LOGGER.info("Hit enter to stop it...");
            // This is a bypass for starting in nohup mode under UNIX in case when
            // System.in.read() throws an IOException
            try {
                System.in.read();
                unDeploy();
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

    private static void unDeploy() {
        deploymentManager.getDeployment().getServletContainer().listDeployments().forEach(deployment -> {
            LOGGER.info("Undeploying : " + deployment);
            deploymentManager.getDeployment().getServletContainer().getDeployment(deployment).undeploy();
        });
    }

    private static IdentityManager initialiseIdentityManager() {
        Map<String, char[]> userCredentials = new HashMap<>();
        userCredentials.put("appuser", "appuser123".toCharArray());
        userCredentials.put("appadmin", "admin123".toCharArray());

        Map<String, Set<String>> userRoles = new HashMap<>();
        userRoles.put("appuser", Set.of("user"));
        userRoles.put("appadmin", Set.of("admin"));

        MapIdentityManager mapIdentityManager = new MapIdentityManager(userCredentials, userRoles);
        return mapIdentityManager;
    }

    private static SecurityConstraint initializeSecurityConstraint(String urlPattern, String roleAllowed) {
        WebResourceCollection webResourceCollection = new WebResourceCollection();
        webResourceCollection.addUrlPatterns(urlPattern);
        SecurityConstraint secureConstraint = new SecurityConstraint();
        secureConstraint.addWebResourceCollection(webResourceCollection);
        if (nonNull(roleAllowed)) {
            secureConstraint.addRoleAllowed(roleAllowed);
        }
        return secureConstraint;
    }


    private static LoginConfig initialiseLoginConfig(String securityRealm, String authorizationMethod) {
        AuthMethodConfig authConfig = new AuthMethodConfig(authorizationMethod);
        LoginConfig loginConfig = new LoginConfig(securityRealm);
        loginConfig.getAuthMethods().add(authConfig);
        return loginConfig;
    }

    private static DeploymentManager initialiseDeploymentManager(ServletContainerInitializerInfo sciInfo) throws Exception {
        //addClassPath(".");
        DeploymentInfo servletBuilder = Servlets.deployment()
                .setDeploymentName("embedded-poc")
                .setClassLoader(Main.class.getClassLoader())
                .setResourceManager(new ClassPathResourceManager(Main.class.getClassLoader(), "META-INF/resources"))
                .setContextPath(CONTEXT_PATH)
                .addSecurityConstraints(
                        /*
                        initializeSecurityConstraint("/h2console/*", null),
                        */
                        initializeSecurityConstraint("/*", "user")
                )
                .setIdentityManager(identityManager)
                .setLoginConfig(initialiseLoginConfig("Embedded Poc", "BASIC"))
                .setDefaultEncoding(StandardCharsets.UTF_8.name())
                .setUrlEncoding(StandardCharsets.UTF_8.name())
                .setDefaultServletConfig(new DefaultServletConfig(true, Set.of("resolve-against-context-root")))
                .addWelcomePage("index.html")
                .addServletContainerInitializers(sciInfo);

        //servletBuilder.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, new WebSocketDeploymentInfo());
        DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(servletBuilder);
        deploymentManager.deploy();
        return deploymentManager;
    }


    private static ServletContainerInitializerInfo injectSpringWebAppInitializerIntoUndertowContainer() {
        // add additional WebAppInitializers if needed
        return new ServletContainerInitializerInfo(
                SpringServletContainerInitializer.class,
                new ImmediateInstanceFactory<>(new SpringServletContainerInitializer()),
                Set.of(Bootstrap.class)
        );
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
        if (Objects.isNull(stream)) {
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