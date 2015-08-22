package com.dirkmanske.httpserver.core.http.request;

import com.dirkmanske.httpserver.core.http.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains all http request information.
 *
 * @author dirkmanske
 */
public class HttpRequest {

    private static final Logger LOGGER = Logger.getLogger(HttpRequest.class.getName());

    public HttpRequestWrapper parse(final String request) throws IOException {
        LOGGER.info(request);

        HttpRequestWrapper result = null;

        if (request != null) {
            BufferedReader reader = new BufferedReader(new StringReader(request));
            RequestLine requestLine = new RequestLine();
            requestLine.parse(reader.readLine());

            RequestHeader requestHeader = new RequestHeader();
            String line;
            while ((line = reader.readLine()) != null) {
                requestHeader.parse(line);
            }

            LOGGER.info(requestLine.toString());
            LOGGER.info(requestHeader.toString());

            result = new HttpRequestWrapper(requestLine, requestHeader);
        }

        return result;
    }

    public static final class HttpRequestWrapper {

        private final RequestLine requestLine;

        private final RequestHeader requestHeader;

        public HttpRequestWrapper(final RequestLine requestLine, final RequestHeader requestHeader) {
            this.requestLine = requestLine;
            this.requestHeader = requestHeader;
        }

        public RequestLine getRequestLine() {
            return requestLine;
        }

        public RequestHeader getRequestHeader() {
            return requestHeader;
        }

    }

    public static class RequestHeader {

        private static final String regex = "^([\\w-]+):\\s*(.*)$";

        private static final Pattern pattern = Pattern.compile(regex);

        private final Map<HttpRequestHeader, String> headers = new HashMap<>();

        RequestHeader() {
        }

        void parse(final String request) {
            Matcher matcher = pattern.matcher(request);
            if (matcher.matches()) {
                HttpRequestHeader header = HttpRequestHeader.resolve(matcher.group(1));
                if (header != null) {
                    headers.put(header, matcher.group(2));
                }
            }
        }

        public Map<HttpRequestHeader, String> getAllHeaders() {
            return headers;
        }

        public String getHeaderField(HttpRequestHeader header) {
            return headers.get(header);
        }

        @Override
        public String toString() {
            return "RequestHeader{" + "headers=" + headers + '}';
        }

    }

    public static class RequestLine {

        private static final String regex = "^([\\w]+)\\s([\\w]*.*[\\w]*)\\s(HTTP/1\\.[01])$";

        private static final Pattern pattern = Pattern.compile(regex);

        private static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");

        private HttpMethod method;

        private String resource;

        private String version;

        RequestLine() {
        }

        void parse(final String request) {
            Matcher matcher = pattern.matcher(request);
            if (matcher.matches()) {
                try {
                    this.method = HttpMethod.resolve(matcher.group(1));
                    this.resource = URLDecoder.decode(matcher.group(2), DEFAULT_CHARSET.name()).replaceFirst("/", "");
                    this.version = matcher.group(3);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Could not parse request line", ex);
                }
            }
        }

        public HttpMethod getMethod() {
            return method;
        }

        public String getResource() {
            return resource;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "RequestLine{" + "method=" + method + ", resource=" + resource + ", version=" + version + '}';
        }

    }
}
