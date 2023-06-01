package com.dragontree.odyssey.generator;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class ToyCatalogGenerator {

    public static void main(String[] args) {

        ToyCatalogGenerator generator = new ToyCatalogGenerator();
        generator.generate();
    }

    public void generate() {
        Path localPublishDir = new File("/tmp/hollow/toys").toPath();

        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localPublishDir);
        HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localPublishDir);

        HollowProducer producer = HollowProducer
                .withPublisher(publisher)
                .withAnnouncer(announcer)
                .build();

        producer.initializeDataModel(Toy.class);

        List<Toy> toys = buildToyCollection();
        producer.runCycle(state -> {
            toys.forEach(t -> state.add(t));
        });

    }


    @HollowPrimaryKey(fields="id")
    public static class Toy {
        private String id;
        private String name;

        private String color;

        public Toy(String id) {
            this.id = id;
        }

        public Toy(String id, String name, String color) {
            this.id = id;
            this.name = name;
            this.color = color;
        }

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

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    private List<Toy> buildToyCollection() {

        List<Toy> toys = new LinkedList<>();

        int id = 101;

        for(int a = 0; a < Animals.size(); a++) {
            String animal = Animals.get(a);

            for(int c = 0; c < Colors.size(); c++) {
                String color = Colors.get(c);

                Toy toy = new Toy(String.valueOf(id++), animal, color);
                toys.add(toy);
            }
        }
        return toys;
    }

}
