package com.dragontreesoftware.odyssey.generator;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class IntegerTransformGenerator {

    public static void main(String[] args) {

        IntegerTransformGenerator generator = new IntegerTransformGenerator();
        generator.generate();
    }

    public void generate() {
        Path localPublishDir = new File("/tmp/hollow/numbers").toPath();

        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localPublishDir);
        HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localPublishDir);

        HollowProducer producer = HollowProducer
                .withPublisher(publisher)
                .withAnnouncer(announcer)
                .build();

        producer.initializeDataModel(Transforms.class);

        List<Transforms> toys = buildTransformCollection();
        producer.runCycle(state -> toys.forEach(t -> state.add(t)));

    }


    @HollowPrimaryKey(fields="number")
    public static class Transforms {
        private final Integer number;

        private final Integer square;
        private final Integer cube;
        private final Integer quad;
        private final Double squareRoot;
        private final Double log10;
        private final String hex;

        public Transforms(Integer num) {
            this.number = num;
            this.square = num * num;
            this.cube = square * num;
            this.quad = cube * num;
            this.squareRoot = Math.sqrt(num.doubleValue());
            this.log10 = Math.log10((num.doubleValue()));
            this.hex = String.format("0%x", num);
        }

    }

    private List<Transforms> buildTransformCollection() {

        List<Transforms> transforms = new LinkedList<>();

        for(int i = 0; i < 1000; i++) {
            transforms.add(new Transforms(i));
        }

        return transforms;
    }

}
