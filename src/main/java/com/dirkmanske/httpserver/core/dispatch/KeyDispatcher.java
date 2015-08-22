package com.dirkmanske.httpserver.core.dispatch;

import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import com.dirkmanske.httpserver.core.worker.HttpWorker;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Manages worker thread pool and submits new workers when connection is accepted.
 *
 * @author dirkmanske
 */
public class KeyDispatcher {

//    private static final Logger LOGGER = Logger.getLogger((KeyDispatcher.class.getName()));
    private final ThreadPoolExecutor executor;

    public KeyDispatcher(final ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public void dispatch(final SelectionKey selectionKey) {
        if (selectionKey != null && selectionKey.isAcceptable()) {
            executor.submit(new HttpWorker(selectionKey, new HttpRequest()));
        }
    }

}
