package com.dirkmanske.httpserver.core.worker;

import com.dirkmanske.httpserver.core.http.request.HttpRequest;
import com.dirkmanske.httpserver.core.http.request.handler.MethodHandler;
import com.dirkmanske.httpserver.core.http.request.handler.RequestHandlerFactory;
import com.dirkmanske.httpserver.core.http.response.HttpResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Worker for receiving request and sending response.
 *
 * @author dirk.manske
 */
public class HttpWorker implements Callable<Void> {

    private static final Logger LOGGER = Logger.getLogger(HttpWorker.class.getName());

    private final Charset charset = Charset.forName("ISO-8859-1");

    private final CharsetDecoder decoder = charset.newDecoder();

    private final ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    private final SelectionKey selectionKey;

    private final HttpRequest requestParser;

    private final String name;

    public HttpWorker(final SelectionKey selectionKey, final HttpRequest requestParser) {
        this.selectionKey = selectionKey;
        this.requestParser = requestParser;
        this.name = "http-worker-" + Thread.currentThread().getName() + this;
    }

    @Override
    public Void call() throws Exception {
        ServerSocketChannel clientConnection = (ServerSocketChannel) selectionKey.channel();
        SocketChannel channel = clientConnection.accept();

        if (channel != null) {
            channel.configureBlocking(false);

            LOGGER.info(String.format("%s accepted client connection from %s", name, channel.getRemoteAddress()));

            try {
                final HttpRequest.HttpRequestWrapper requestWrapper = requestParser.parse(read(channel));

                MethodHandler handler = RequestHandlerFactory.newInstance(requestWrapper.getRequestLine().getMethod());
                HttpResponse.HttpResponseWrapper responseWrapper = handler.handle(requestWrapper);
                write(channel, responseWrapper);
            } finally {
                channel.close();
            }

            LOGGER.info(String.format("%s is done.", name));
        }

        return null;
    }

    String read(final SocketChannel channel) throws IOException {
        StringBuilder result = new StringBuilder();

        int bytesRead = channel.read(readBuffer);
        while (bytesRead > 0) {
            readBuffer.rewind();
            bytesRead = channel.read(readBuffer);
            readBuffer.rewind();
            result.append(decoder.decode(readBuffer));
            readBuffer.clear();
        }

        if (bytesRead == -1) {
            selectionKey.cancel();
        }

        LOGGER.info(String.format("%s read done.", name));
        return result.toString();
    }

    void write(final SocketChannel channel, final HttpResponse.HttpResponseWrapper responseWrapper) throws IOException {
        ByteBuffer response = responseWrapper.getResponse();

        int writtenBytes = 0;
        while (response.hasRemaining()) {
            writtenBytes += channel.write(response);
        }

        LOGGER.log(Level.INFO, "Written bytes: {0}", writtenBytes);
    }

}
