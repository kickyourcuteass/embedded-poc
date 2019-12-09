package ro.home.server.properties;


public class ServerProperties {

    public static final String JASYPT_PASS = "JASYPT_PASS";
    public static final String JASYPT_ENCRYPTION_KEY = (System.getenv(JASYPT_PASS) != null) ? System.getenv(JASYPT_PASS) : System.getProperty(JASYPT_PASS);

    //public static final Integer HTTPS_PORT = Integer.valueOf(System.getProperty("https.port"));
    //public static final String KEY_STORE_PATH = System.getProperty("key.store.path");
    //public static final String KEY_STORE_PASSWORD = System.getProperty("key.store.password");
    //public static final String TRUST_STORE_PATH = System.getProperty("trust.store.path");

    public static final Integer DEFAULT_SERVER_PORT = 9090;

    public static void checkInputParameters() {
        /*
        Assert.notNull(JASYPT_ENCRYPTION_KEY, "No encryption key found for JASYPT... Please define an environment " +
                " or a system variable named JASYPT_PASS !!!");
        */
        /*
        Assert.notNull(HTTPS_PORT, "No https.port value has been found. Please define a system variable called https.port !!!");
        Assert.notNull(KEY_STORE_PATH, "No key store file path has been found... Please get a valid .jks keystore and " +
                "input its path as a system variable called key.store.path !!!");
        Assert.notNull(KEY_STORE_PASSWORD, "No key store password has been found... Please define a system variable called " +
                "key.store.password containing the JASYPT encrypted key store password !!!");

        */
    }

}
