package com.dragontreesoftware.odyssey.templates.freemarker;

import com.dragontreesoftware.odyssey.core.HollowReader;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResolverTest {

    @Disabled
    @Test
    void mainIT() throws ResolverException {
        Resolver resolver = new Resolver.Builder()
                .withClass(Resolver.class, "/templates")
                .withHollowDirectory(Resolver.class.getResource("/hollow/templates/interjections"))
                .build();
        demo(resolver);
    }

    private void demo(Resolver resolver) throws ResolverException {
        try {

            Map<String, String> data = new HashMap<>();
            data.put("name", "Odyssey");

            System.out.println("\nhello.ftlh");
            System.out.println(resolver.resolve("hello.ftlh", data));

            System.out.println("\ninterjection-hola.ftlh");
            System.out.println(resolver.resolve("interjection-hola.ftlh", data));

            System.out.println("\ninterjection-hola.ftlh");
            System.out.println(resolver.resolve("interjection-hola.ftlh", data));

            System.out.println("\ninterjection-adios.ftlh");
            System.out.println(resolver.resolve("interjection-adios.ftlh", data));

        } catch(Exception e) {
            throw new ResolverException(e);
        }
    }

    private static TemplateLoader buildTemplateLoader() throws URISyntaxException {

        URL hollowUrl = Resolver.class.getResource("/hollow/templates/interjections");

        // reader takes path to folder, not snapshot file
        HollowReader reader = HollowReader.load(Paths.get(hollowUrl.toURI()));


        TemplateLoader[] loaders = {
                new ClassTemplateLoader(Resolver.class, "/templates"),
                new HollowTemplateReader(reader)
        };
        MultiTemplateLoader loader = new MultiTemplateLoader(loaders);
        return loader;
    }
}