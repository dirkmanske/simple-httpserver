package com.dirkmanske.httpserver.core.resource;

import com.dirkmanske.httpserver.core.http.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resource handler for images.
 *
 * @author dirkmanske
 */
public class ImageResourceHandler extends AbstractResourceHandler {

    private static final Logger LOGGER = Logger.getLogger(ImageResourceHandler.class.getName());

    @Override
    public boolean canHandle(final MediaType mediaType) {
        return mediaType != null && (MediaType.ALL.equals(mediaType) || mediaType.getType().startsWith("image"));
    }

    @Override
    public ByteBuffer handle(final URI resource) throws IOException {
        ByteBuffer result = null;

        if (resource != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[16384];
            int bytesRead;

            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                try (InputStream is = resource.toURL().openStream()) {
                    DigestInputStream dis = new DigestInputStream(is, md);
                    while ((bytesRead = dis.read(buffer)) >= 0) {
                        baos.write(buffer, 0, bytesRead);
                    }
                }

                this.hash = convertHash(md.digest());
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.log(Level.SEVERE, "Could not calculate hash", ex.getMessage());
            }

            byte[] byteArray = baos.toByteArray();
            LOGGER.info(String.format("Read %s bytes for resource %s", byteArray.length, resource.getPath()));

            result = ByteBuffer.wrap(byteArray).asReadOnlyBuffer();
        }

        return result;
    }

}
