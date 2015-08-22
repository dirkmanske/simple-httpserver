package com.dirkmanske.httpserver.core.http;

/**
 *
 * @author dirkmanske
 */
public enum HttpMethod {

    OPTIONS("OPTIONS"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private final String method;

    private HttpMethod(final String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static HttpMethod resolve(final String lookup) {
        HttpMethod result = null;

        if (lookup != null) {
            try {
                result = valueOf(lookup);
            } catch (IllegalArgumentException ex) {
                // ignore
            }
        }

        return result;
    }

}
