package com.dirkmanske.httpserver.core.resource;

import com.dirkmanske.httpserver.core.http.MediaType;

/**
 *
 * @author dirkmanske
 */
public final class ResourceHandlerFactory {

    private ResourceHandlerFactory() {
    }

    public static ResourceHandler newInstance(final MediaType mediaType) {
        ResourceHandler result = null;

        if (mediaType != null) {
            switch (mediaType) {
                case TEXT_HTML:
                    result = new TextResourceHandler();
                    break;
                default:
                    result = new ImageResourceHandler();
            }
        }

        return result;
    }

}
