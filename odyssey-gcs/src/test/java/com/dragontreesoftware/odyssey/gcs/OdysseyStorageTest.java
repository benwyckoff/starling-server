package com.dragontreesoftware.odyssey.gcs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class OdysseyStorageTest {


    @Test
    void relativePath() {

        Path prefix = Paths.get("/gs", "bucket1");
        Path expected = Paths.get("foo", "bar");

        Path p0 = Paths.get("/gs", "bucket1", "foo", "bar");

        Path p1 = Paths.get("foo", "bar");

        assertEquals(expected, OdysseyGcsStorage.relativePath(prefix, p0));
        assertEquals(expected, OdysseyGcsStorage.relativePath(prefix, p1));
    }

    @Disabled
    @Test
    void WalkGcsStorageIT() {
        OdysseyGcsStorage storage = new OdysseyGcsStorage("starling-server-odyssey");
        storage.walk(Paths.get("hollow"), b -> {
            System.out.println(b.asBlobInfo().toString());
            return true;
        });
    }

    // reference code.
    protected void scanStorageForSnapshots(OdysseyGcsStorage storage) {
        storage.walk(Paths.get("hollow/"), b -> {
            boolean ok = b.getBlobId().getName().matches(".*snapshot-\\d+");
            if(ok) {
                System.out.println("accept " + b.getBlobId().toGsUtilUri());
            } else {
                System.out.println("reject " + b.getBlobId().toGsUtilUri());
            }
            // it's a predicate, and false will bail on the whole walk
            return true;
        });
    }


}