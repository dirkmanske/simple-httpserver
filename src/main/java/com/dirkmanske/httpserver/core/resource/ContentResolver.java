package com.dirkmanske.httpserver.core.resource;

import com.dirkmanske.httpserver.core.config.Configuration;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;

/**
 * Resolves docroot, copies test files etc.
 *
 * @author dirk.manske
 */
public final class ContentResolver {

    private static final Logger LOGGER = Logger.getLogger(ContentResolver.class.getName());

    public static final String ERROR_PAGE = "error.html";

    public static final String DEFAULT_PAGE = "index.html";

    private static final String IMAGE_PAGE = "image.html";

    private static final String IMAGE_PAGE2 = "images.html";

    private static final String IMAGE_RESOURCE = "adobe.png";

    private static final String IMAGE_RESOURCE2 = "500k1.jpg";

    private static final String IMAGE_RESOURCE3 = "500k2.jpg";

    private static final String IMAGE_RESOURCE4 = "500k3.jpg";

    private static final ContentResolver INSTANCE = new ContentResolver();

    private final Configuration config = Configuration.getInstance();

    private final Lock lock = new ReentrantLock();

    private volatile Path docroot;

    private ContentResolver() {
    }

    public static ContentResolver getInstance() {
        return INSTANCE;
    }

    public void resolveDocroot(final String directory) throws IOException {
        lock.lock();

        String tmpDir = directory;
        try {
            final URI uri = ContentResolver.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            tmpDir = Paths.get(uri).getParent().resolve(config.getString(Configuration.DOCROOT)).toFile().getAbsolutePath();

            this.docroot = createDirectory(tmpDir);
            LOGGER.info(String.format("Docroot is %s", docroot.toString()));
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, "Could not get server location");
            throw new IOException(ex);
        } finally {
            lock.unlock();
        }
    }

    public Path getDocroot() {
        return docroot;
    }

    public Path createDirectory(final String dir) throws IOException {
        lock.lock();

        try {
            if (dir == null) {
                throw new IllegalArgumentException("Directory path must be provided");
            }

            Path path = Paths.get(dir);
            if (Files.exists(path)) {
                return path;
            }
            Path created = Files.createDirectories(path);
            if (!Files.exists(created)) {
                throw new IIOException(String.format("Unable to create directory %s", dir));
            }

            copyTo(created.resolve(DEFAULT_PAGE), getClass().getResourceAsStream("/" + Configuration.DOCROOT + "/" + DEFAULT_PAGE));
            copyTo(created.resolve(ERROR_PAGE), getClass().getResourceAsStream("/" + Configuration.DOCROOT + "/" + ERROR_PAGE));

            // Test files; TODO refactor this, use CopyFileVisitor
//            copyTo(created.resolve(IMAGE_PAGE), getClass().getResourceAsStream("/" + Configuration.DOCROOT + "/" + IMAGE_PAGE));
//            copyTo(created.resolve(IMAGE_PAGE2), getClass().getResourceAsStream("/" + Configuration.DOCROOT + "/" + IMAGE_PAGE2));
//            copyTo(created.resolve(IMAGE_RESOURCE), getClass().getResourceAsStream("/" + Configuration.DOCROOT + "/" + IMAGE_RESOURCE));
//            copyTo(created.resolve(IMAGE_RESOURCE2), getClass().getResourceAsStream("/" + Configuration.DOCROOT + "/" + IMAGE_RESOURCE2));
//            copyTo(created.resolve(IMAGE_RESOURCE3), getClass().getResourceAsStream("/" + Configuration.DOCROOT + "/" + IMAGE_RESOURCE3));
//            copyTo(created.resolve(IMAGE_RESOURCE4), getClass().getResourceAsStream("/" + Configuration.DOCROOT + "/" + IMAGE_RESOURCE4));
            return created;
        } finally {
            lock.unlock();
        }
    }

    public URI getResource(final String resource) {
        URI result = null;

        if (resource != null) {
            result = getDocroot().resolve(resource).toAbsolutePath().toUri();
        }

        return result;
    }

    private void copyDir(final Path source, final Path target) throws IOException {
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                Integer.MAX_VALUE, new CopyFileVisitor(docroot, docroot));
    }

    private void copyTo(Path path, InputStream is) throws IOException {
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
    }

    private static final class CopyFileVisitor extends SimpleFileVisitor<Path> {

        private final Path source;

        private final Path target;

        public CopyFileVisitor(final Path source, final Path target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                throws IOException {
            LOGGER.log(Level.INFO, "Copy file {0}", source.relativize(file));
            Files.copy(file, target.resolve(source.relativize(file)));
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path directory,
                BasicFileAttributes attributes) throws IOException {
            Path targetDirectory = target.resolve(source.relativize(directory));
            try {
                LOGGER.log(Level.INFO, "Copy dir {0}", source.relativize(directory));
                Files.copy(directory, targetDirectory);
            } catch (FileAlreadyExistsException e) {
                if (!Files.isDirectory(targetDirectory)) {
                    throw e;
                }
            }
            return FileVisitResult.CONTINUE;
        }

    }

}
