package com.dragontree.odyssey.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HollowReaderService {

    private Map<String, HollowReader<? extends Object>> hollowReaders = new ConcurrentHashMap<>();

    public HollowReaderService() {
        getHollowPaths().forEach(this::loadReader);
    }

    public List<String> getTypes() {
        return hollowReaders.keySet().stream().toList();
    }

    public List<String> getTypedKeys(String type) {
        HollowReader reader = hollowReaders.get(type);
        if(reader == null) {
            return Collections.emptyList();
        }

        return reader.getPrimaryKeys();
    }
    public String get(String id) {
        return hollowReaders.values().stream().map(reader -> reader.getRecordAsJsonFromString(id)).findFirst().orElse(null);
    }

    public String getFromOrdinal(int id) {
        return hollowReaders.values().stream().map(reader -> reader.getRecordAsJsonFromOrdinal(id)).findFirst().orElse(null);
    }

    public String get(String type, String id) {
        HollowReader reader = hollowReaders.get(type);
        if(reader != null) {
            return reader.getRecordAsJsonFromString(id);
        }
        return null;
    }

    public String getFromOrdinal(String type, int id) {
        HollowReader reader = hollowReaders.get(type);
        if(reader != null) {
            return reader.getRecordAsJsonFromOrdinal(id);
        }
        return null;
    }

    private static List<Path> getHollowPaths() {
        List<Path> paths = new LinkedList<>();
        String setting = System.getProperty("hollowPaths", System.getenv("HOLLOW_PATHS"));
        if(setting == null) {
            paths.add(Paths.get("/tmp/hollow/"));
        } else {
            String[] names = setting.split(",");
            paths.addAll(Arrays.stream(names).map(String::strip).map(Paths::get).toList());
        }

        Set<Path> distinctPaths = new TreeSet<>(paths);

        paths.forEach(p -> {
            distinctPaths.addAll(listDirs(p));
        });

        return new ArrayList<>(distinctPaths);
    }

    private static Set<Path> listDirs(Path dir) {
        Set<Path> paths = new HashSet<>();
        File file = dir.toFile();
        if(file.exists() && file.isDirectory()) {
            paths.add(dir);
            for(File sub : file.listFiles(f -> f.isDirectory())) {
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
