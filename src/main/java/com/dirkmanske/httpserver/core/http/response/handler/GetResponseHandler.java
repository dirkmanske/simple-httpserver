package com.dirkmanske.httpserver.core.http.response.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;
import com.dirkmanske.httpserver.core.http.HttpStatus;
import com.dirkmanske.httpserver.core.http.MediaType;
import com.dirkmanske.httpserver.core.http.request.HttpRequestHeader;
import com.dirkmanske.httpserver.core.http.response.HttpResponseHeader;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Service instance for handling GET responses.
 *
 * @author dirkmanske
 */
public class GetResponseHandler extends AbstractResponseHandler {

    private static final Logger LOGGER = Logger.getLogger(GetResponseHandler.class.getName());

    @Override
    public boolean canHandle(HttpMethod method) {
        return HttpMethod.GET.equals(method);
    }

    @Override
    protected ByteBuffer process() throws Exception {
        ConditionalRequest conditionalRequest = new ConditionalRequest(messageBody.getHash());

        HttpRequestHeader conditionalHeader = conditionalRequest.hasOneOf(HttpRequestHeader.IF_NONE_MATCH, HttpRequestHeader.IF_MATCH,
                HttpRequestHeader.IF_MODIFIED_SINCE);
        if (conditionalHeader != null) {
            conditionalRequest.handle(conditionalHeader);
        }

        if (!HttpStatus.OK.equals(statusLine.getStatus())) {
            return createResponse(statusLine.getByteBuffer(), responseHeader.getByteBuffer(), null);
        } else {
            return createResponse(statusLine.getByteBuffer(), responseHeader.getByteBuffer(), messageBody.getByteBuffer());
        }
    }

    @Override
    protected ByteBuffer createResponse(final ByteBuffer statusLineBuffer, final ByteBuffer responseHeaderBuffer,
            final ByteBuffer messageBuffer) {
        ByteBuffer response = ByteBuffer.allocate(statusLineBuffer.capacity() + responseHeaderBuffer.capacity() + (messageBuffer != null
                                                                                                                           ? messageBuffer.
                capacity() : 0));

        response.put(statusLineBuffer);
        response.put(responseHeaderBuffer);

        if (messageBuffer != null) {
            response.put(messageBuffer);
        }

        response.flip();

        return response;
    }

    @Override
    protected ResponseHeader createResponseHeader(int contentLength, MediaType mediaType) {
        return new ExtendedResponseHeader(contentLength, mediaType);
    }

    protected class ExtendedResponseHeader extends ResponseHeader {

        public ExtendedResponseHeader(final int contentLength, final MediaType mediaType) {
            super(contentLength, mediaType);
        }

        @Override
        protected String getAsString() {
            String headers = super.getAsString();

            StringBuilder sb = new StringBuilder(headers);
            if (!HttpStatus.NOT_MODIFIED.equals(statusLine.getStatus()) && messageBody != null && messageBody.getHash() != null) {
                sb.append(HttpResponseHeader.ETAG.getHeader()).append(": ").append(messageBody.getHash());
            }

            return sb.toString();
        }

    }

    protected class ConditionalRequest {

        private final String hash;

        public ConditionalRequest(final String hash) {
            this.hash = hash;
        }

        protected HttpRequestHeader hasOneOf(HttpRequestHeader... headers) {
            HttpRequestHeader result = null;

            for (HttpRequestHeader header : headers) {
                if (result == null && requestWrapper.getRequestHeader().getHeaderField(header) != null) {
                    result = header;
                }
            }
            return result;
        }

        protected void handle(HttpRequestHeader header) {
            if (match(requestWrapper.getRequestHeader().getHeaderField(HttpRequestHeader.IF_NONE_MATCH))) {
                statusLine.setStatus(HttpStatus.NOT_MODIFIED);
            } else if (!match(requestWrapper.getRequestHeader().getHeaderField(HttpRequestHeader.IF_MATCH))) {
                statusLine.setStatus(HttpStatus.PRECONDITION_FAILED);
            }
        }

        protected boolean match(final String headerField) {
            return hash != null && hash.equals(headerField);
        }

    }

}
