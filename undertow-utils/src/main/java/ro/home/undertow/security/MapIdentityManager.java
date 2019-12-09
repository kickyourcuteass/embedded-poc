package ro.home.undertow.security;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class MapIdentityManager implements IdentityManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapIdentityManager.class);

    private final Map<String, char[]> userCredentials;
    private final Map<String, Account> userAccounts;

    public MapIdentityManager(final Map<String, char[]> userCredentials, final Map<String, Set<String>> userRoles) {
        this.userCredentials = userCredentials;
        this.userAccounts = userRoles.entrySet().stream().map(
                entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), new Account() {
                    private final Principal principal = entry::getKey;
                    private final Set<String> userRoles = entry.getValue();

                    @Override
                    public Principal getPrincipal() {
                        return principal;
                    }

                    @Override
                    public Set<String> getRoles() {
                        return userRoles;
                    }
                }))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @Override
    public Account verify(String userId, Credential credential) {
        Account account = userAccounts.get(userId);
        if (nonNull(account) && verifyCredential(account, credential)) {
            return account;
        }
        return null;
    }

    @Override
    public Account verify(Account account) {
        // An existing account so for testing assume still valid.
        return account;
    }

    @Override
    public Account verify(Credential credential) {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean verifyCredential(Account account, Credential credential) {
        if (credential instanceof PasswordCredential) {
            char[] password = ((PasswordCredential) credential).getPassword();
            char[] expectedPassword = userCredentials.get(account.getPrincipal().getName());
            Boolean check = Arrays.equals(password, expectedPassword);
            LOGGER.info("Password credential check : {}", check);
            return check;
        }
        return false;
    }

}

