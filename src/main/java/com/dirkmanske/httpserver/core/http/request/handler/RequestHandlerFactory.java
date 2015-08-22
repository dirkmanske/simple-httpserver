package com.dirkmanske.httpserver.core.http.request.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;

/**
 *
 * @author dirkmanske
 */
public class RequestHandlerFactory {

    public static MethodHandler newInstance(final HttpMethod httpMethod) {
        MethodHandler result = null;

        if (httpMethod != null) {
            switch (httpMethod) {
                case GET:
                    result = new GetMethodHandler();
                    break;
                case HEAD:
                    result = new HeadMethodHandler();
                    break;
                default:
                    // http method not supported
                    result = new DefaultMethodHandler();
            }
        }

        return result;
    }

}
