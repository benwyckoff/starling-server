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

    private final Map<HollowReaderKey, HollowReader<? extends Object>> hollowReaders = new ConcurrentHashMap<>();
    private final List<Path> knownPaths = new ArrayList<>();

    private ScheduledExecutorService scheduler;

    private HollowReaderFactory hollowReaderFactory = new DefaultHollowReaderFactory();
    private IdleHollowReaderListener idleListener;

    public HollowReaders() {
    }

    public HollowReaders(List<Path> paths) {
        knownPaths.addAll(paths);
        // Maybe load, but don't open until needed...
        paths.forEach(this::loadReader);
    }

    // TODO look at methods for re-loading/updating paths


    public HollowReaderFactory getHollowReaderFactory() {
        return hollowReaderFactory;
    }

    public void setHollowReaderFactory(HollowReaderFactory hollowReaderFactory) {
        this.hollowReaderFactory = hollowReaderFactory;
    }

    public void setIdleTime(long time, TimeUnit units) {
        if(scheduler != null) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> this.closeIdleReaders(time, units), time,time, units);
    }

    public IdleHollowReaderListener getIdleListener() {
        return idleListener;
    }

    public void setIdleListener(IdleHollowReaderListener idleListener) {
        this.idleListener = idleListener;
    }

    public void closeIdleReaders(long maxIdle, TimeUnit units) {
        long now = System.currentTimeMillis();
        long maxIdleMillis = units.toMillis(maxIdle);
        List<HollowReader<?>> closed = new LinkedList<>();
        hollowReaders.entrySet().forEach(e -> {
            HollowReader<?> reader = e.getValue();
            long idleMillis = reader.idleTime(now);
            if (idleMillis > maxIdleMillis) {
                reader.close();
                closed.add(reader);
            }
        });
        if(idleListener != null) {
            closed.forEach(r -> idleListener.notify(r));
        }
    }

    public void scan() {
        scan(knownPaths, true);
    }

    public void scan(List<Path> paths, boolean load) {
        Set<Path> distinctPaths = new TreeSet<>(knownPaths);
        // expand the storage/reader factory to provide list, maybe.
        paths.forEach(p -> distinctPaths.addAll(listDirs(p)));

        knownPaths.clear();
        knownPaths.addAll(distinctPaths);
        if(load) {
            paths.forEach(this::loadReader);
        }
    }

    public void scan(Path... paths) {
        scan(Arrays.asList(paths), true);
    }

    public boolean add(Path p) {
        return loadReader(p);
    }

    public boolean remove(Path p) {
        HollowReader reader = this.getHollowReader(p.toString());
        if(reader != null) {
            this.hollowReaders.remove(reader);
            reader.close();
            return true;
        }
        return false;
    }


    public List<HollowReaderKey> getTypes() {
        return hollowReaders.keySet().stream().toList();
    }

    public HollowReader getHollowReader(String type) {
        return findReader(type);
    }

    public List<HollowReaderKey> getTypedKeys(String type) {
        return getTypedKeys(type, 0, Integer.MAX_VALUE);
    }

    public List<HollowReaderKey> getTypedKeys(String type, int from, int numKeys) {
        HollowReader<? extends Object> reader = getHollowReader(type);
        if(reader == null) {
            return Collections.emptyList();
        }

        final String primaryType = reader.getPrimaryType();
        return reader.getPrimaryKeys(from, numKeys).stream()
                .map(k -> new HollowReaderKey(reader.getHollowPath().toString(), primaryType, k))
                .toList();
    }

    public String get(String id) {
        return hollowReaders.values().stream().map(reader -> reader.getRecordAsJsonFromString(id)).findFirst().orElse(null);
    }

    public String getFromOrdinal(int id) {
        return hollowReaders.values().stream().map(reader -> reader.getRecordAsJsonFromOrdinal(id)).findFirst().orElse(null);
    }

    public String get(String type, String id) {
        HollowReader<? extends Object> reader = getHollowReader(type);
        if(reader != null) {
            return reader.getRecordAsJsonFromString(id);
        }
        return null;
    }

    public String getFromOrdinal(String type, int id) {
        HollowReader<? extends Object> reader = getHollowReader(type);
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

    private HollowReaderKey buildReadersKey(Path p, String type) {
        String path = p.toString();
        return new HollowReaderKey(path, type);
    }

    private boolean loadReader(Path p) {
        try {
            HollowReader<? extends Object> reader = hollowReaderFactory.load(p);
            if (reader != null) {
                hollowReaders.put(buildReadersKey(reader.getHollowPath(), reader.getPrimaryType()), reader);
                return true;
            }
        } catch(Throwable t) {
            t.printStackTrace();
            // log failure
        }
        return false;
    }

    private HollowReader findReader(String type) {
        HollowReader reader = null;
        List<? extends HollowReader<?>> readers = findReaders(type);
        if (readers.size() == 1) {
            reader = readers.get(0);
        } else if (readers.size() == 0) {
            reader = null;
        } else {
            throw new IllegalStateException("Multiple potential reader matches found: " + readers);
        }
        return reader;
    }

    private List<? extends HollowReader<?>> findReaders(String type) {
        // the map key is an object. Assume type here is type, or idHash, or path (unlikely)
        List<? extends HollowReader<?>> readers = hollowReaders.entrySet().stream()
                .filter(e -> type.equals(e.getKey().getType())).map(e -> e.getValue()).toList();

        if(readers.isEmpty()) {
            readers = hollowReaders.entrySet().stream()
                    .filter(e -> type.equals(e.getKey().getIdHash())).map(e -> e.getValue()).toList();
        }

        if(readers.isEmpty()) {
            readers = hollowReaders.entrySet().stream()
                    .filter(e -> type.equals(e.getKey().getHollowPath())).map(e -> e.getValue()).toList();
        }

        return readers;
    }

}
