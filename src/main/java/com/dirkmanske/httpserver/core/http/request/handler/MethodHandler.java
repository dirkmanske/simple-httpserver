package com.dirkmanske.httpserver.core.http.request.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;
import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import com.dirkmanske.httpserver.core.http.response.HttpResponse;

/**
 * All http request method handler inherit from this class.
 *
 * @author dirkmanske
 */
public interface MethodHandler {

    boolean canHandle(final HttpMethod method);

    HttpResponse.HttpResponseWrapper handle(final HttpRequest.HttpRequestWrapper requestWrapper);

}
