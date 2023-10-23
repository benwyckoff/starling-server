package com.dragontreesoftware.odyssey.gcs;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class OdysseyGcsStorage {

    private final String bucketName;
    private final Path pathPrefix;

    private static final Storage GCS_STORAGE = StorageOptions.getDefaultInstance().getService();

    public OdysseyGcsStorage(String bucketName) {
        this.bucketName = bucketName;
        pathPrefix = Paths.get("/gs", bucketName);
    }



    // move
    public static Path relativePath(Path prefix, Path src) {
        if(src.startsWith(prefix)) {
            return prefix.relativize(src);
        }
        return src;
    }

    public Path relativePath(Path src) {
        return relativePath(pathPrefix, src);
    }

    public Path absolutePath(Path src) {
        if (src.startsWith(pathPrefix)) {
            return src;
        }
        return pathPrefix.resolve(src);
    }

    /**
     * return true if the provided path is absolute and matches this storage prefix
     * or if it is a relative path. This does not guarantee that the referenced file exists.
     * @param src source Path
     * @return true if the provided path is absolute and matches this storage prefix
     *      * or if it is a relative path
     */
    public boolean validPath(Path src) {
        return src.startsWith(pathPrefix) || src.getRoot() == null;
    }

    public boolean delete(Path gcs) {
        BlobId blobId = BlobId.of(bucketName, relativePath(gcs).toString());
        return GCS_STORAGE.delete(blobId );
    }

    public boolean exists(Path dest) {
        BlobId blobId = BlobId.of(bucketName, relativePath(dest).toString());

        Blob blob = GCS_STORAGE.get(blobId);
        return blob != null && blob.exists();
    }

    public List<Path> list(Path dir, int limit) {

        List<Path> paths = new LinkedList<>();
        int pathCount = 0;
        // recursive/follow-folders by default

        Page<Blob> blobs = GCS_STORAGE.list(bucketName, Storage.BlobListOption.prefix(relativePath(dir).toString()));
        Iterator<Blob> blobIterator = blobs.iterateAll().iterator();
        while (blobIterator.hasNext() && pathCount < limit) {
            Blob blob = blobIterator.next();
            // do something with the blob
            String name = blob.getName();
            paths.add(Paths.get(name));
            pathCount += 1;
        }
        return paths;
    }

    public void walk(Path dir, Predicate<Blob> consumer) {

        // recursive/follow-folders by default

        Page<Blob> blobs = GCS_STORAGE.list(bucketName, Storage.BlobListOption.prefix(relativePath(dir).toString()));
        Iterator<Blob> blobIterator = blobs.iterateAll().iterator();
        while (blobIterator.hasNext()) {
            Blob blob = blobIterator.next();
            if(!consumer.test(blob)) {
                break;
            }
        }
    }

    public void walkCurrentDirectory(Path dir, Predicate<Blob> consumer) {

        Page<Blob> blobs = GCS_STORAGE.list(bucketName, Storage.BlobListOption.prefix(relativePath(dir).toString() + "/"),
                Storage.BlobListOption.currentDirectory());
        Iterator<Blob> blobIterator = blobs.iterateAll().iterator();
        while (blobIterator.hasNext()) {
            Blob blob = blobIterator.next();
            if(!consumer.test(blob)) {
                break;
            }
        }
    }

    public InputStream getInputStream(Path src) {
        BlobId blobId = BlobId.of(bucketName, relativePath(src).toString());

        Blob blob = GCS_STORAGE.get(blobId);
        if(blob != null && blob.exists()) {
            ReadChannel channel = blob.reader();

            return Channels.newInputStream(channel);
        }
        return null;
    }

}
