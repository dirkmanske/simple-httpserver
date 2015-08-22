package com.dirkmanske.httpserver.core.http.response.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;

/**
 *
 * @author dirkmanske
 */
public final class ResponseHandlerFactory {

    private ResponseHandlerFactory() {
    }

    public static ResponseHandler newInstance(final HttpMethod httpMethod) {
        ResponseHandler result = null;

        if (httpMethod != null) {
            switch (httpMethod) {
                case GET:
                    result = new GetResponseHandler();
                    break;
                case HEAD:
                    result = new HeadResponseHandler();
                    break;
                default:
                    // http method not supported
                    result = new DefaultResponseHandler();
            }
        }

        return result;
    }

}
