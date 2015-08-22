package com.dirkmanske.httpserver.core.resource;

/**
 *
 * @author dirkmanske
 */
public abstract class AbstractResourceHandler implements ResourceHandler {

    protected String hash;

    protected String convertHash(final byte[] digest) {
        String result = null;
        if (digest != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            result = sb.toString();
        }

        return result;
    }

    @Override
    public String getHash() {
        return hash;
    }

}
