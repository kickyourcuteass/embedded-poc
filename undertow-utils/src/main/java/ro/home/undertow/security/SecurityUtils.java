package ro.home.undertow.security;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpHandler;
import ro.home.undertow.handler.AuthorizationHandler;

import java.util.List;

public class SecurityUtils {

    public static HttpHandler addSecurity(HttpHandler targetHandler,
                                          String allowedRole,
                                          List<AuthenticationMechanism> authenticationMechanisms,
                                          IdentityManager identityManager
    ) {
        HttpHandler handler = targetHandler;
        handler = new AuthorizationHandler(handler, allowedRole);
        handler = new AuthenticationCallHandler(handler);
        handler = new AuthenticationConstraintHandler(handler);
        handler = new AuthenticationMechanismsHandler(handler, authenticationMechanisms);
        return new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler);
    }
}
