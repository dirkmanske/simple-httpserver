package com.dirkmanske.httpserver.core.http.response;

import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import com.dirkmanske.httpserver.core.http.response.handler.ResponseHandler;
import com.dirkmanske.httpserver.core.http.response.handler.ResponseHandlerFactory;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * Prepares response by delegating to ResponseHandler. Contains http response information.
 *
 * @author dirkmanske
 */
public class HttpResponse {

    public HttpResponseWrapper prepareResponse(final HttpRequest.HttpRequestWrapper requestWrapper, final URI resource) {
        ResponseHandler handler = ResponseHandlerFactory.newInstance(requestWrapper.getRequestLine().getMethod());
        ByteBuffer result = handler.handle(requestWrapper, resource);

        return new HttpResponseWrapper(result);
    }

    public static final class HttpResponseWrapper {

        private final ByteBuffer byteBuffer;

        public HttpResponseWrapper(final ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
        }

        public ByteBuffer getResponse() {
            return byteBuffer;
        }

    }

}
