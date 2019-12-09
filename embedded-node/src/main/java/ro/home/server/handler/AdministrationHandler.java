package ro.home.server.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.home.Main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.String.format;

public class AdministrationHandler implements HttpHandler {

    private static final String SHUTDOWN_PATH = "/shutdown";
    private static final String REDEPLOY_PATH =  "/redeploy";
    private static final String UNDEPLOY_PATH = "/undeploy";

    private static final String SCHEDULED_SHUTDOWN = "Server's graceful shutdown has been successfully initiated...";
    private static final String UNAUTHORIZED_METHOD = "Request's method %s is unauthorized inside administration handler!";
    private static final String UNAVAILABLE_ACTION = "The requested action is not available inside the administration handler !";

    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationHandler.class);

    private final Method stopServer;
    private final Method unDeployApps;

    public AdministrationHandler() throws NoSuchMethodException {
        this.stopServer = Main.class.getDeclaredMethod("stop");
        this.stopServer.setAccessible(true);
        this.unDeployApps = Main.class.getDeclaredMethod("unDeploy");
        this.unDeployApps.setAccessible(true);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws InvocationTargetException, IllegalAccessException{
        String httpMethod = exchange.getRequestMethod().toString();
        switch (httpMethod) {
            case "POST":
                dispatchRequest(exchange);
                break;
            default:
                exchange.setResponseCode(401);
                exchange.getResponseSender().send(format(UNAUTHORIZED_METHOD,  httpMethod));
                break;
        }
    }

    private void dispatchRequest(HttpServerExchange exchange) throws IllegalAccessException, InvocationTargetException {
        String relativePath = exchange.getRelativePath();
        LOGGER.info("Request's Relative Path  : " + relativePath);
        if (SHUTDOWN_PATH.equals(relativePath)){
            new Thread(() -> {
                try {
                    unDeployApps.invoke(null);
                    stopServer.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e){
                    LOGGER.error("Graceful shut down action has failed! Reason : ", e);
                }
            }).start();
            exchange.setResponseCode(200);
            exchange.getResponseSender().send(SCHEDULED_SHUTDOWN);
            return;
        }
        if (relativePath.startsWith(UNDEPLOY_PATH)){
            unDeployApps.invoke(null);
            return;
        }
        if (relativePath.startsWith(REDEPLOY_PATH)){
            return;
        }
        exchange.setResponseCode(501);
        exchange.getResponseSender().send(UNAVAILABLE_ACTION);
    }

}

