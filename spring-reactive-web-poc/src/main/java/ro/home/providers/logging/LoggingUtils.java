package ro.home.providers.logging;

import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.String.join;

public class LoggingUtils {

    public static void logRequestMetadata(Logger logger, CachingServerHttpRequestDecorator request) {
        logger.debug(join("\n",
                "\nREQUEST                :",
                format("%s%s", "METHOD                 : ", request.getMethodValue()),
                format("%s%s", "URI                    : ", request.getURI().getPath()),
                format("%s%s", "URL                    : ", uriToURLString(request.getURI())),
                format("%s%s%s", "HEADERS                : [\n",
                        request.getHeaders().entrySet()
                                .stream()
                                .map(entry -> format("\t\t%s : %s", format("%-25s", entry.getKey()), join(", ", entry.getValue())))
                                .collect(Collectors.joining("\n")),
                        "\n]"
                ),
                request.getCachedBody().isEmpty() ? "" : format("%s%s%s", "BODY                   :\n", request.getCachedBody(), "\n")
        ));
    }

    public static void logResponseMetadata(Logger logger, CachingServerHttpResponseDecorator response) {
        logger.debug(join("\n",
                "\nRESPONSE               :",
                format("%s%s", "CODE                   : ", response.getStatusCode().toString()),
                format("%s%s%s", "HEADERS                : [\n",
                        response.getHeaders().entrySet()
                                .stream()
                                .map(entry -> format("\t\t%s : %s", format("%-25s", entry.getKey()), join(", ", entry.getValue())))
                                .collect(Collectors.joining("\n")),
                        "\n]"
                ),
                response.getCachedBody().isEmpty() ? "" : format("%s%s%s", "BODY                   : ", response.getCachedBody(), "\n")
        ));
    }

    private static String uriToURLString(URI requestUri) {
        try {
            return requestUri.toURL().toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
