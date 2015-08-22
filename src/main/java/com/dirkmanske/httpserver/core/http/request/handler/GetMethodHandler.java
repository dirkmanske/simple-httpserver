package com.dirkmanske.httpserver.core.http.request.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;
import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import com.dirkmanske.httpserver.core.http.response.HttpResponse;
import com.dirkmanske.httpserver.core.resource.ContentResolver;
import java.net.URI;
import java.util.logging.Logger;

/**
 * Service instance for handling GET requests.
 *
 * @author dirkmanske
 */
public class GetMethodHandler implements MethodHandler {

    private static final Logger LOGGER = Logger.getLogger(GetMethodHandler.class.getName());

    protected final ContentResolver resolver = ContentResolver.getInstance();

    @Override
    public boolean canHandle(final HttpMethod method) {
        return HttpMethod.GET.equals(method);
    }

    @Override
    public HttpResponse.HttpResponseWrapper handle(final HttpRequest.HttpRequestWrapper requestWrapper) {
        HttpResponse.HttpResponseWrapper responseWrapper = null;
        if (requestWrapper != null && requestWrapper.getRequestLine() != null) {
            String requestedResource = requestWrapper.getRequestLine().getResource();
            URI uri = getResource(requestedResource);
            if (uri != null) {
                LOGGER.info(String.format("Servicing %s request for resource %s", requestWrapper.getRequestLine().getMethod().getMethod(),
                        uri.toString()));
            }

            HttpResponse response = new HttpResponse();
            responseWrapper = response.prepareResponse(requestWrapper, uri);
        }

        return responseWrapper;
    }

    protected URI getResource(String requestedResource) {
        return resolver.getResource(requestedResource);
    }

}
