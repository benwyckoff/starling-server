package com.dragontreesoftware.odyssey.generator;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DataSetGenerator3 {

    public static void main(String[] args) {

        DataSetGenerator3 generator = new DataSetGenerator3();
        generator.test();
    }

    public void test() {
        Path localPublishDir = new File("/tmp/hollow/combo").toPath();

        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localPublishDir);
        HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localPublishDir);

        HollowProducer producer = HollowProducer
                .withPublisher(publisher)
                .withAnnouncer(announcer)
                .build();

        producer.initializeDataModel(Combo.class);

        producer.runCycle(state -> {
            for(int i = 100; i < 132; i++) {
                state.add(buildCombo(i));
            }
        });

    }



    @HollowPrimaryKey(fields={"name","size","group"})
    public static class Combo {
        private String name;
        private String size;
        private int group;

        private List<String> items = new LinkedList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

    private Combo buildCombo(int id) {
        Random rand = new Random();

        Combo combo = new Combo();
        combo.setName("Combo-" + id/2);
        combo.setSize( id % 2 == 0 ? "small" : "large");

        int imod = id % 17;
        combo.setGroup(imod);

        combo.getItems().add(imod < 8 ? "burger" : "chicken");
        combo.getItems().add(imod % 4 < 2 ? "fries" : "onion-rings");
        combo.getItems().add(imod % 11 < 6 ? "water" : "soda");

        return combo;
    }

}
