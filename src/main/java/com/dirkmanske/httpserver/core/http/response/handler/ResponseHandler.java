package com.dirkmanske.httpserver.core.http.response.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;
import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * All http response handler inherit from this class.
 *
 * @author dirkmanske
 */
public interface ResponseHandler {

    boolean canHandle(final HttpMethod method);

    ByteBuffer handle(HttpRequest.HttpRequestWrapper requestWrapper, final URI resource);

}
