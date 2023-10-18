package com.dragontreesoftware.odyssey.generator;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.io.File;
import java.nio.file.Path;

public class HollowFreemarkerTemplateGenerator {

    public static void main(String[] args) {

        HollowFreemarkerTemplateGenerator generator = new HollowFreemarkerTemplateGenerator();
        generator.test();
    }

    public void test() {
        Path localPublishDir = new File("/tmp/hollow/template/interjections").toPath();

        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localPublishDir);
        HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localPublishDir);

        HollowProducer producer = HollowProducer
                .withPublisher(publisher)
                .withAnnouncer(announcer)
                .build();

        producer.initializeDataModel(Template.class);

        producer.runCycle(state -> {
            state.add(new Template("interjection-hello.ftlh", "Hello ${name}!"));
            state.add(new Template("interjection-goodbye.ftlh", "Goodbye ${name}!"));
            state.add(new Template("interjection-hola.ftlh", "!Hola ${name}"));
            state.add(new Template("interjection-adios.ftlh", "!Adios ${name}"));
        });

    }

    @HollowPrimaryKey(fields="name")
    public static final class Template {
        private String name;
        private String body;

        public Template(String name, String body) {
            this.name = name;
            this.body = body;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}
