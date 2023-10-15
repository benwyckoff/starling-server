package com.dragontreesoftware.odyssey.core;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HollowReaders {

    private final Map<String, HollowReader<? extends Object>> hollowReaders = new ConcurrentHashMap<>();
    private final List<Path> knownPaths = new ArrayList<>();

    private ScheduledExecutorService scheduler;// = Executors.newSingleThreadScheduledExecutor();

    public HollowReaders() {
    }

    public HollowReaders(List<Path> paths) {
        knownPaths.addAll(paths);
        // Maybe load, but don't open until needed...
        paths.forEach(this::loadReader);
    }

    // TODO look at methods for re-loading/updating paths

    // so, if something is closed as idle, how to we "re-open" or "re-discover" the hollow data set?

    public void setIdleTime(long time, TimeUnit units) {
        if(scheduler != null) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> this.closeIdleReaders(time, units), time,time, units);
    }

    public void closeIdleReaders(long maxIdle, TimeUnit units) {
        long now = System.currentTimeMillis();
        long maxIdleMillis = units.toMillis(maxIdle);
        hollowReaders.entrySet().forEach(e -> {
            HollowReader<?> reader = e.getValue();
            long idleMillis = reader.idleTime(now);
            if (idleMillis > maxIdleMillis) {
                reader.close();
            }
        });
    }

    public void scan() {
        scan(knownPaths);
    }

    public void scan(List<Path> paths) {
        Set<Path> distinctPaths = new TreeSet<>(knownPaths);
        paths.forEach(p -> distinctPaths.addAll(listDirs(p)));

        knownPaths.clear();
        knownPaths.addAll(distinctPaths);
    }

    public List<String> getTypes() {
        return hollowReaders.keySet().stream().toList();
    }

    public HollowReader getHollowReader(String type) {
        return hollowReaders.get(type);
    }

    public List<String> getTypedKeys(String type) {
        HollowReader<? extends Object> reader = hollowReaders.get(type);
        if(reader == null) {
            return Collections.emptyList();
        }

        return reader.getPrimaryKeys();
    }

    public List<String> getTypedKeys(String type, int from, int numKeys) {
        HollowReader<? extends Object> reader = hollowReaders.get(type);
        if(reader == null) {
            return Collections.emptyList();
        }

        return reader.getPrimaryKeys(from, numKeys);
    }

    public String get(String id) {
        return hollowReaders.values().stream().map(reader -> reader.getRecordAsJsonFromString(id)).findFirst().orElse(null);
    }

    public String getFromOrdinal(int id) {
        return hollowReaders.values().stream().map(reader -> reader.getRecordAsJsonFromOrdinal(id)).findFirst().orElse(null);
    }

    public String get(String type, String id) {
        HollowReader<? extends Object> reader = hollowReaders.get(type);
        if(reader != null) {
            return reader.getRecordAsJsonFromString(id);
        }
        return null;
    }

    public String getFromOrdinal(String type, int id) {
        HollowReader<? extends Object> reader = hollowReaders.get(type);
        if(reader != null) {
            return reader.getRecordAsJsonFromOrdinal(id);
        }
        return null;
    }

    public static List<Path> getDefaultHollowPaths() {
        List<Path> paths = new LinkedList<>();
        String setting = System.getProperty("hollowPaths", System.getenv("HOLLOW_PATHS"));
        if(setting == null) {
            paths.add(Paths.get("/tmp/hollow/"));
        } else {
            String[] names = setting.split(",");
            paths.addAll(Arrays.stream(names).map(String::strip).map(Paths::get).toList());
        }

        Set<Path> distinctPaths = new TreeSet<>(paths);

        paths.forEach(p -> distinctPaths.addAll(listDirs(p)));

        return new ArrayList<>(distinctPaths);
    }

    private static Set<Path> listDirs(Path dir) {
        Set<Path> paths = new HashSet<>();
        File file = dir.toFile();
        if(file.exists() && file.isDirectory()) {
            paths.add(dir);
            for(File sub : Objects.requireNonNull(file.listFiles(f -> f.isDirectory()))) {
                paths.addAll(listDirs(sub.toPath()));
            }
        }
        return paths;
    }

    private void loadReader(Path p) {
        try {
            HollowReader<? extends Object> reader = HollowReader.load(p);
            if (reader != null) {
                hollowReaders.put(reader.getPrimaryType(), reader);
            }
        } catch(Throwable t) {
            // log failure
        }
    }
}
