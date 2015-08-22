/*
 *  Bauer Systems KG
 *
 */
package com.dirkmanske.httpserver.core.http.response.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;
import com.dirkmanske.httpserver.core.http.HttpStatus;
import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service instance for handling all unimplemented http method requests.
 *
 * @author dirkmanske
 */
public class DefaultResponseHandler extends AbstractResponseHandler {

    @Override
    public ByteBuffer handle(final HttpRequest.HttpRequestWrapper requestWrapper, final URI resource) {
        ByteBuffer result = null;
        try {
            result = createErrorResponse(HttpStatus.NOT_IMPLEMENTED, requestWrapper.getRequestLine().getVersion());
        } catch (IOException ex) {
            Logger.getLogger(DefaultResponseHandler.class.getName()).log(Level.SEVERE, "Could prepare response", ex);
        }

        return result;
    }

    @Override
    protected ByteBuffer createResponse(final ByteBuffer statusLineBuffer, final ByteBuffer responseHeaderBuffer,
            final ByteBuffer messageBuffer) {
        ByteBuffer response = ByteBuffer.allocate(statusLineBuffer.capacity() + responseHeaderBuffer.capacity() + messageBuffer.capacity());

        response.put(statusLineBuffer);
        response.put(responseHeaderBuffer);
        response.put(messageBuffer);

        response.flip();

        return response;
    }

    @Override
    public boolean canHandle(final HttpMethod method) {
        return true;
    }

}
