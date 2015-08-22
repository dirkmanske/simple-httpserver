package com.dirkmanske.httpserver.core.http.request.handler;

import com.dirkmanske.httpserver.core.http.HttpMethod;

/**
 * Service instance for handling HEAD requests. Processes essentially the same data as GETMethodHandler. Differs in that HeadMethodHandler
 * does not use MessageBody in response.
 *
 * @author dirkmanske
 */
public class HeadMethodHandler extends GetMethodHandler {

    @Override
    public boolean canHandle(final HttpMethod method) {
        return HttpMethod.HEAD.equals(method);
    }

}
