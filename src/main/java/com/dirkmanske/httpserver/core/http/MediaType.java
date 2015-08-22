package com.dirkmanske.httpserver.core.http;

/**
 *
 * @author dirkmanske
 */
public enum MediaType {

    TEXT_HTML("text/html"),
    IMAGE_GIF("image/gif"),
    IMAGE_PNG("image/png"),
    IMAGE_JPG("image/jpeg"),
    IMAGE_WEBP("image/webp"),
    ALL("*/*");

    private final String type;

    private MediaType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static MediaType resolve(final String lookup) {
        MediaType result = null;

        if (lookup != null) {
            for (MediaType type : values()) {
                if (result == null && lookup.contains(type.getType())) {
                    result = type;
                }
            }
        }

        return result;
    }

    public static MediaType getMediaType(String filename) {
        MediaType result = null;

        if (filename != null) {
            for (MediaType mediaType : values()) {
                if (result == null && filename.endsWith(mediaType.getType().substring(mediaType.getType().lastIndexOf("/") + 1))) {
                    result = mediaType;
                }
            }
        }

        return result;
    }

}
