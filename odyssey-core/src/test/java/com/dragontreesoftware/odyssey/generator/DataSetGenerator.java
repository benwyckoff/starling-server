package com.dragontreesoftware.odyssey.generator;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class DataSetGenerator {

    public static void main(String[] args) {

        DataSetGenerator generator = new DataSetGenerator();
        generator.test();
    }

    public void test() {
        Path localPublishDir = new File("/tmp/hollow/kit").toPath();

        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localPublishDir);
        HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localPublishDir);

        HollowProducer producer = HollowProducer
                .withPublisher(publisher)
                .withAnnouncer(announcer)
                .build();

        producer.initializeDataModel(Kit.class, Part.class, Span.class, Screw.class, Bolt.class);

        producer.runCycle(state -> {
            for(int i = 100; i < 120; i++) {
                state.add(buildTestKit(i));
            }
        });

    }

    //record Screw(String name, int size) { }
    //record Bolt(String name, int size) { }
    //record Span(String name, int len, String color) { }

    public static class Part {
        private String name;
        private int size;
        private String color;

        public Part() {
        }

        public Part(String name, int size, String color) {
            this.name = name;
            this.size = size;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class Screw extends Part {
        public Screw(String name, int size) {
            super(name, size, null);
        }
    }
    public static class Bolt extends Part {
        public Bolt(String name, int size) {
            super(name, size, null);
        }
    }
    public static class Span extends Part {
        public Span(String name, int size, String color) {
            super(name, size, color);
        }
    }

    public static final class Count {
        int num = 0;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int incrementAndGet() {
            this.num += 1;
            return num;
        }

        public int addAndGet(int add) {
            this.num += add;
            return num;
        }
    }

    @HollowPrimaryKey(fields="name")
    public static final class Kit {
        private String id = UUID.randomUUID().toString();
        private String name;
        private Map<Part,Count> partsList = new HashMap<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<Part, Count> getPartsList() {
            return partsList;
        }

        public void setPartsList(Map<Part, Count> partsList) {
            this.partsList = partsList;
        }

        public void addPart(Part part, int count) {
            partsList.computeIfAbsent(part, p -> new Count()).addAndGet(count);
        }
    }

    private Kit buildTestKit(int id) {
        Kit kit = new Kit();
        kit.setName("kit-" + id);

        int delta = id % 7;

        kit.addPart(new Span("Leg", 30 + delta, id % 2 == 0 ? "red" : "blue"), 4);
        kit.addPart(new Bolt("B-" + id, 6), 4);
        kit.addPart(new Span("Seat", 30 + delta, "yellow"), 1);
        kit.addPart(new Screw("S-" + id, 3), 4);

        kit.addPart(new Part("back", 9, "orange"), 1);

        return kit;
    }

}
