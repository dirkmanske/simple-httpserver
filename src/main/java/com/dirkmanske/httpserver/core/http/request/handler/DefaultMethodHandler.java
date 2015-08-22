package com.dirkmanske.httpserver.core.http.request.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;
import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import com.dirkmanske.httpserver.core.http.response.HttpResponse;

/**
 *
 * @author dirkmanske
 */
public class DefaultMethodHandler implements MethodHandler {

    @Override
    public boolean canHandle(HttpMethod method) {
        return true;
    }

    @Override
    public HttpResponse.HttpResponseWrapper handle(HttpRequest.HttpRequestWrapper requestWrapper) {
        HttpResponse response = new HttpResponse();
        return response.prepareResponse(requestWrapper, null);
    }

}
