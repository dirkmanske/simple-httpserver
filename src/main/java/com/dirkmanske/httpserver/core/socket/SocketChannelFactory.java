package com.dirkmanske.httpserver.core.socket;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

/**
 * Serves new asynchronous socket channels.
 *
 * @author dirk.manske
 */
public final class SocketChannelFactory {

    private static final Logger LOGGER = Logger.getLogger(SocketChannelFactory.class.getName());

    private static final SocketChannelFactory INSTANCE = new SocketChannelFactory();

    private SocketChannelFactory() {
    }

    public static SocketChannelFactory getInstance() {
        return INSTANCE;
    }

    public ServerSocketChannel newServerSocketChannel(final SocketAddress address) throws IOException {
        LOGGER.info(String.format("Open connection on %s", address));

        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(address);

        return channel;
    }

}
