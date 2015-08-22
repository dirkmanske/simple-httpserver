package com.dirkmanske.httpserver.core.http.response.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;
import java.nio.ByteBuffer;

/**
 * Service instance for handling HEAD responses.
 *
 * @author dirkmanske
 */
public class HeadResponseHandler extends AbstractResponseHandler {

    @Override
    public boolean canHandle(HttpMethod method) {
        return HttpMethod.HEAD.equals(method);
    }

    @Override
    protected ByteBuffer createResponse(final ByteBuffer statusLineBuffer, final ByteBuffer responseHeaderBuffer,
            final ByteBuffer messageBuffer) {
        ByteBuffer response = ByteBuffer.allocate(statusLineBuffer.capacity() + responseHeaderBuffer.capacity());

        response.put(statusLineBuffer);
        response.put(responseHeaderBuffer);

        response.flip();

        return response;
    }

}
