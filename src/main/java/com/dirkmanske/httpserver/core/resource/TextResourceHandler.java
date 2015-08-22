package com.dirkmanske.httpserver.core.resource;

import com.dirkmanske.httpserver.core.http.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resource handler for text and directory listings.
 *
 * @author dirkmanske
 */
public class TextResourceHandler extends AbstractResourceHandler {

    private static final Logger LOGGER = Logger.getLogger(TextResourceHandler.class.getName());

    private final Charset charset = Charset.forName("ISO-8859-1");

    private final CharsetEncoder encoder = charset.newEncoder();

    @Override
    public boolean canHandle(MediaType mediaType) {
        return MediaType.TEXT_HTML.equals(mediaType);
    }

    @Override
    public ByteBuffer handle(final URI resource) throws IOException {
        ByteBuffer result = null;

        if (resource != null) {
            Processor processor = ProcessorFactory.newProcessor(resource);
            processor.setTextResourceHandler(this);
            result = encoder.encode(CharBuffer.wrap(processor.readResource(resource)));
        }

        return result;
    }

    private static final class ProcessorFactory {

        static Processor newProcessor(final URI resource) {

            // check if resource is directory
            int lastIndexOf = resource.toString().lastIndexOf(".") + 1;
            if (lastIndexOf == resource.toString().length() - 4 || lastIndexOf == resource.toString().length() - 3) {
                return new HtmlProcessor();
            } else {
                return new DirectoryProcessor();
            }
        }

    }

    private static abstract class Processor {

        protected TextResourceHandler handler;

        abstract String preProcess();

        abstract String afterProcess();

        abstract String process(final String text);

        String readResource(final URI resource) throws IOException {
            String result = null;

            try {
                StringBuilder sb = new StringBuilder();
                sb.append(preProcess());

                URLConnection conn = resource.toURL().openConnection();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(process(line));
                    }
                }

                sb.append(afterProcess());
                result = sb.toString();
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.SEVERE, "URI resource mismatch", ex);
            }

            return result;
        }

        void setTextResourceHandler(final TextResourceHandler handler) {
            this.handler = handler;
        }

    }

    private static final class HtmlProcessor extends Processor {

        @Override
        String preProcess() {
            return "";
        }

        @Override
        String afterProcess() {
            return "";
        }

        @Override
        String process(final String text) {
            if (text != null && handler != null) {
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    handler.hash = handler.convertHash(md.digest(text.getBytes("UTF-8")));
                } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                    LOGGER.log(Level.SEVERE, "Could not calculate hash", ex);
                }
            }
            return text;
        }

    }

    private static final class DirectoryProcessor extends Processor {

        private static final String HTML_START_SEQ = "<!DOCTYPE html><html><head><title>Directory</title><body>";

        private static final String HTML_CONTENT = "<h2>Index of DIR</h2><ul>";

        private static final String HTML_END_SEQ = "</ul></body></html>";

        @Override
        String preProcess() {
            return HTML_START_SEQ + HTML_CONTENT;
        }

        @Override
        String afterProcess() {
            return HTML_END_SEQ;
        }

        @Override
        String process(final String text) {
            MediaType mediaType = MediaType.getMediaType(text);
            return "<li><a href=\"" + text + "\"" + (mediaType != null ? " type=\"" + mediaType.getType() + "\"" : "") + ">" + text
                    + "</a></li>";
        }

        @Override
        String readResource(final URI resource) throws IOException {
            String processed = super.readResource(resource);
            String dir = resource.getPath().substring(0, resource.getPath().length() - 1);
            dir = dir.substring(dir.lastIndexOf("/"));
            String result = processed.replaceAll("DIR", dir);

            return result;
        }

    }
}
