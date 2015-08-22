package com.dirkmanske.httpserver.core.http;

/**
 *
 * @author dirkmanske
 */
public enum HttpStatus {

    OK("OK", 200),
    NO_CONTENT("No Content", 204),
    NOT_MODIFIED("Not Modified", 304),
    NOT_FOUND("Not Found", 404),
    PRECONDITION_FAILED("Precondition Failed", 412),
    SERVER_ERROR("Internal Server Error", 500),
    NOT_IMPLEMENTED("Not Implemented", 501);

    private final String status;

    private final int code;

    private HttpStatus(final String status, final int code) {
        this.status = status;
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

}
