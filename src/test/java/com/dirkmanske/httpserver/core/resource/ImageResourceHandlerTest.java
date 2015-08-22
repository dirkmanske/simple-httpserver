package com.dirkmanske.httpserver.core.resource;

import static org.assertj.core.api.Assertions.assertThat;
import com.dirkmanske.httpserver.core.http.MediaType;
import java.net.URI;
import java.nio.ByteBuffer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dirkmanske
 */
public class ImageResourceHandlerTest {

    private ImageResourceHandler handler;

    public ImageResourceHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.handler = new ImageResourceHandler();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCanHandle() throws Exception {
        assertThat(handler.canHandle(MediaType.IMAGE_GIF));
        assertThat(handler.canHandle(MediaType.IMAGE_JPG));
        assertThat(handler.canHandle(MediaType.IMAGE_PNG));
        assertThat(handler.canHandle(MediaType.IMAGE_WEBP));
    }

    @Test
    public void testCreateByteBuffer() throws Exception {
        URI resource = getClass().getResource("adobe.png").toURI();
        assertThat(resource).isNotNull();
        ByteBuffer bb = handler.handle(resource);
        assertThat(bb).isNotNull();
    }

}
