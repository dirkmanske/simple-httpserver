package com.dirkmanske.httpserver.core;

import com.dirkmanske.httpserver.core.config.Configuration;
import com.dirkmanske.httpserver.core.dispatch.KeyDispatcher;
import com.dirkmanske.httpserver.core.listener.HttpListener;
import com.dirkmanske.httpserver.core.resource.ContentResolver;
import com.dirkmanske.httpserver.core.socket.SocketChannelFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class. Server is being configured and started. Use a shutdown hook to clean up resources on close.
 *
 * @author dirk.manske
 */
public final class HttpServer {

    private static final Logger LOGGER = Logger.getLogger(HttpServer.class.getName());

    private final ServerSocketChannel channel;

    private final ThreadPoolExecutor listenersExecutor;

    private final ThreadPoolExecutor workersExecutor;

    private final String docroot;

    public static void main(String[] args) throws IOException {
        String docroot = null;

        if (args.length > 0) {
            if (!args[0].startsWith(Configuration.DOCROOT)) {
                usage();
            } else {
                docroot = args[0].substring(args[0].indexOf("=") + 1);
            }
        }

        final Configuration config = Configuration.getInstance();
        HttpServer server = new HttpServer(config, docroot);
        server.start();
    }

    private static void usage() {
        LOGGER.info("[Usage] java -jar ... [docroot=your/path]");
        System.exit(0);
    }

    private HttpServer(final Configuration config, String docroot) throws IOException {
        registerShutdownHook();

        this.docroot = docroot;

        this.listenersExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getInt(
                Configuration.LISTENER_THREADS));
        this.workersExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getInt(
                Configuration.WORKER_THREADS));

        LOGGER.info(String.format("Created http-listener thread pool [size %s]", listenersExecutor.getMaximumPoolSize()));
        LOGGER.info(String.format("Created http-worker thread pool [size %s]", workersExecutor.getMaximumPoolSize()));

        this.channel = SocketChannelFactory.getInstance().newServerSocketChannel(new InetSocketAddress(config.
                getInt(Configuration.LISTENER_PORT)));
    }

    private void start() throws IOException {
        ContentResolver.getInstance().resolveDocroot(docroot);

        HttpListener listener = new HttpListener(channel, Selector.open(), new KeyDispatcher(workersExecutor));
        listenersExecutor.submit(listener);

        LOGGER.info("HttpServer started");
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                LOGGER.info("Shutdown HttpServer..");

                if (listenersExecutor != null) {
                    listenersExecutor.shutdown();
                }
                if (workersExecutor != null) {
                    workersExecutor.shutdown();
                }
                if (channel != null && channel.isOpen()) {
                    try {
                        channel.close();
                    } catch (IOException ex) {
                        LOGGER.log(Level.WARNING, "Unable to shutdown server properly.", ex);
                    }
                }
                LOGGER.info("Good bye.");
            }

        });
    }

}
