package com.dirkmanske.httpserver.core.http.response;

/**
 *
 * @author dirkmanske
 */
public enum HttpResponseHeader {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    DATE("Date"),
    CONNECTION("Connection"),
    ETAG("Etag");

    private final String header;

    private HttpResponseHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

}
