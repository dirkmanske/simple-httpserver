package com.dirkmanske.httpserver.core.listener;

import com.dirkmanske.httpserver.core.dispatch.KeyDispatcher;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Listens for client connections.
 *
 * @author dirk.manske
 */
public class HttpListener implements ConnectionListener {

    private static final Logger LOGGER = Logger.getLogger(HttpListener.class.getName());

    private final ServerSocketChannel channel;

    private final Selector selector;

    private final String name;

    public HttpListener(final ServerSocketChannel channel, final Selector selector, final KeyDispatcher dispatcher) throws IOException {
        this.name = "http-listener-" + Thread.currentThread().getName();

        if (!selector.isOpen()) {
            throw new IllegalStateException("selector is not open");
        }

        this.selector = selector;
        this.channel = channel;

        SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(dispatcher);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Void call() throws Exception {
        LOGGER.info(String.format("%s listens for incoming connections.", getName()));

        while (channel.isOpen() && !Thread.interrupted()) {
            selector.select();

            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();
                ((KeyDispatcher) key.attachment()).dispatch(key);
            }
        }

        LOGGER.warning(String.format("%s connection is closed", getName()));

        return null;
    }

}
