package ro.home.undertow.handler;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public class AuthorizationHandler implements HttpHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthorizationHandler.class);
    private static final String UNAUTHORIZED_USER_ROLE = "User %s must have %s role in order to perform this operation !";
    private final String allowedRole;
    private final HttpHandler next;

    public AuthorizationHandler(HttpHandler next, String allowedRole) {
        this.next = next;
        this.allowedRole = allowedRole;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (isNull(allowedRole)) {
            this.next.handleRequest(exchange);
            return;
        }
        Account account = exchange.getSecurityContext().getAuthenticatedAccount();
        if (!account.getRoles().contains(allowedRole)) {
            exchange.setStatusCode(401);
            exchange.getResponseSender().send(format(UNAUTHORIZED_USER_ROLE, account.getPrincipal().getName(), allowedRole));
            return;
        }
        this.next.handleRequest(exchange);
    }

}
