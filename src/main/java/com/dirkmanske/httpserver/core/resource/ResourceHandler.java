package com.dirkmanske.httpserver.core.resource;

import com.dirkmanske.httpserver.core.http.MediaType;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 *
 * @author dirkmanske
 */
public interface ResourceHandler {

    boolean canHandle(final MediaType mediaType);

    ByteBuffer handle(final URI resource) throws IOException;

    String getHash();

}
