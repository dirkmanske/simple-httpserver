package com.dirkmanske.httpserver.core.http.request;

/**
 *
 * @author dirkmanske
 */
public enum HttpRequestHeader {

    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language "),
    CONNECTION("Connection"),
    HOST("Host"),
    IF_MATCH("If-Match"),
    IF_NONE_MATCH("If-None-Match"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    USER_AGENT("User-Agent");

    private final String header;

    private HttpRequestHeader(final String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public static HttpRequestHeader resolve(final String lookup) {
        HttpRequestHeader result = null;

        if (lookup != null) {
            try {
                result = valueOf(lookup.toUpperCase().replaceAll("-", "_"));
            } catch (IllegalArgumentException ex) {
                // ignore
            }
        }

        return result;
    }

}
