package com.dirkmanske.httpserver.core.http.response.handler;

import com.dirkmanske.httpserver.core.http.HttpStatus;
import com.dirkmanske.httpserver.core.http.MediaType;
import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import com.dirkmanske.httpserver.core.http.request.HttpRequestHeader;
import com.dirkmanske.httpserver.core.http.response.HttpResponseHeader;
import com.dirkmanske.httpserver.core.resource.ContentResolver;
import com.dirkmanske.httpserver.core.resource.ResourceHandler;
import com.dirkmanske.httpserver.core.resource.ResourceHandlerFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for all response handlers.
 *
 * @author dirkmanske
 */
public abstract class AbstractResponseHandler implements ResponseHandler {

    private static final Logger LOGGER = Logger.getLogger(AbstractResponseHandler.class.getName());

    protected HttpRequest.HttpRequestWrapper requestWrapper;

    protected StatusLine statusLine;

    protected ResponseHeader responseHeader;

    protected MessageBody messageBody;

    public AbstractResponseHandler() {
    }

    @Override
    public ByteBuffer handle(final HttpRequest.HttpRequestWrapper requestWrapper, final URI resource) {
        this.requestWrapper = requestWrapper;

        ByteBuffer result;
        try {
            MediaType mediaType = MediaType.resolve(requestWrapper.getRequestHeader().getHeaderField(
                    HttpRequestHeader.ACCEPT));
            this.statusLine = createStatusLine(HttpStatus.OK, requestWrapper.getRequestLine().getVersion());
            this.messageBody = createMessageBody(resource, mediaType);
            this.responseHeader = createResponseHeader(messageBody.getContentLength(), mediaType);

            result = process();
        } catch (Throwable ex) {
            result = handleError(requestWrapper, ex);
        }

        return result;
    }

    protected ByteBuffer process() throws Exception {
        return createResponse(statusLine.getByteBuffer(), responseHeader.getByteBuffer(), messageBody.getByteBuffer());
    }

    protected ByteBuffer handleError(final HttpRequest.HttpRequestWrapper requestWrapper, final Throwable ex) {
        ByteBuffer result = null;

        HttpStatus httpStatus;
        if (FileNotFoundException.class.isAssignableFrom(ex.getClass())) {
            httpStatus = HttpStatus.NOT_FOUND;
            LOGGER.log(Level.INFO, "Resource was not found.", ex.getMessage());
        } else {
            httpStatus = HttpStatus.SERVER_ERROR;
            LOGGER.log(Level.SEVERE, "Could not create response.", ex);
        }
        try {
            result = createErrorResponse(httpStatus, requestWrapper.getRequestLine().getVersion());
        } catch (IOException ioex) {
            LOGGER.log(Level.SEVERE, "Could not create error response.", ioex);
        }

        return result;
    }

    protected abstract ByteBuffer createResponse(final ByteBuffer statusLineBuffer, final ByteBuffer responseHeaderBuffer,
            final ByteBuffer messageBuffer);

    protected ByteBuffer createErrorResponse(final HttpStatus httpStatus, final String httpVersion) throws IOException {
        MediaType mediaType = MediaType.TEXT_HTML;
        StatusLine errorStatus = createStatusLine(httpStatus, httpVersion);
        MessageBody errorMessage = createMessageBody(ContentResolver.getInstance().getResource(ContentResolver.ERROR_PAGE), mediaType);
        ResponseHeader errorResponseHeader = createResponseHeader(errorMessage.getContentLength(), mediaType);

        return createResponse(errorStatus.getByteBuffer(), errorResponseHeader.getByteBuffer(), errorMessage.getByteBuffer());
    }

    protected ResponseHeader createResponseHeader(final int contentLength, final MediaType mediaType) {
        return new ResponseHeader(contentLength, mediaType);
    }

    protected StatusLine createStatusLine(final HttpStatus status, final String httpVersion) {
        return new StatusLine(status, httpVersion);
    }

    protected MessageBody createMessageBody(final URI resource, final MediaType mediaType) throws IOException {
        return new MessageBody(resource, mediaType);
    }

    protected static abstract class CharResponse {

        protected final Charset charset = Charset.forName("ISO-8859-1");

        protected final CharsetEncoder encoder = charset.newEncoder();

    }

    protected static class ResponseHeader extends CharResponse {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

        private final int contentLength;

        private final MediaType mediaType;

        protected ResponseHeader(final int contentLength, final MediaType mediaType) {
            this.contentLength = contentLength;
            this.mediaType = mediaType;
        }

        protected String getAsString() {
            StringBuilder sb = new StringBuilder();
            sb.append(HttpResponseHeader.CONTENT_LENGTH.getHeader()).append(": ").append(contentLength).append("\r\n");
            sb.append(HttpResponseHeader.CONTENT_TYPE.getHeader()).append(": ").append(mediaType.getType()).append("\r\n");
            sb.append(HttpResponseHeader.DATE.getHeader()).append(": ").append(dateFormat.format(Calendar.getInstance(TimeZone.getTimeZone(
                    "GMT")).getTime())).append("\r\n");

            return sb.toString();
        }

        protected ByteBuffer getByteBuffer() throws IOException {
            return encoder.encode(CharBuffer.wrap(getAsString() + "\r\n\r\n"));
        }

    }

    protected static class StatusLine extends CharResponse {

        private HttpStatus status;

        private final String httpVersion;

        protected StatusLine(final HttpStatus status, final String httpVersion) {
            this.status = status;
            this.httpVersion = httpVersion;
        }

        protected String getAsString() {
            String line = httpVersion + " " + String.valueOf(status.getCode()) + " " + status.getStatus();
            LOGGER.log(Level.INFO, "Response status {0}", line);
            return line;
        }

        protected ByteBuffer getByteBuffer() throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append(getAsString());
            sb.append("\r\n");

            return encoder.encode(CharBuffer.wrap(sb.toString()));
        }

        protected void setStatus(HttpStatus status) {
            this.status = status;
        }

        protected HttpStatus getStatus() {
            return status;
        }

    }

    protected static class MessageBody {

        private final URI resource;

        private final int contentLength;

        private final String hash;

        private final ByteBuffer byteBuffer;

        protected MessageBody(final URI resource, final MediaType mediaType) throws IOException {
            this.resource = resource;

            if (resource != null) {
                ResourceHandler handler = ResourceHandlerFactory.newInstance(mediaType);
                this.byteBuffer = handler.handle(resource);
                this.contentLength = byteBuffer.capacity();
                this.hash = handler.getHash();

            } else {
                this.byteBuffer = null;
                this.contentLength = 0;
                this.hash = null;
            }
        }

        protected ByteBuffer getByteBuffer() throws IOException {
            return byteBuffer;
        }

        protected int getContentLength() {
            return contentLength;
        }

        protected String getHash() {
            return hash;
        }

        protected URI getResource() {
            return resource;
        }

    }

}
