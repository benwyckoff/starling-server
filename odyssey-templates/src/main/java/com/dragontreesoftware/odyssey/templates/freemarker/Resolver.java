package com.dragontreesoftware.odyssey.templates.freemarker;

import com.dragontreesoftware.odyssey.core.HollowReader;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Resolver {

    private final Configuration freemarkerConfig;

    public Resolver(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    /**
     * find the template and process it with the given dataModel, returning the result as a String
     * @param templateName the name of the template to process
     * @param dataModel the data model to use to resolve the template
     * @return the resolved template as a String
     */
    public String resolve(String templateName, Object dataModel) throws ResolverException {
        StringWriter writer = new StringWriter();
        return resolve(writer, templateName, dataModel).toString();
    }

    /**
     * find the template and process it with the given dataModel, writing the output to the given Writer.
     * The writer is not closed in this method.
     * @param dest the Writer to write to
     * @param templateName the name of the template to process
     * @param dataModel the data model to use to resolve the template
     * @return the provided Writer
     */
    public Writer resolve(Writer dest, String templateName, Object dataModel) throws ResolverException {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            template.process(dataModel, dest);
            return dest;
        } catch (IOException | TemplateException e) {
            throw new ResolverException(e);
        }
    }



    public static class Builder {
        private final Configuration cfg;
        private final List<TemplateLoader> loaders = new LinkedList<>();

        public Builder() {
            this(2,3,0);
        }

        public Builder(int vMajor, int vMinor, int vMicro) {
            cfg = new Configuration(new Version(vMajor,vMinor,vMicro));
            cfg.setDefaultEncoding("UTF-8");
        }

        public Configuration getConfiguration() {
            return cfg;
        }

        public Builder withDefaultEncoding(String enc) {
            cfg.setDefaultEncoding(enc);
            return this;
        }

        public Builder withDirectory(String dir) throws ResolverException {
            return withThis(() -> loaders.add(new FileTemplateLoader(new File(dir))));
        }

        public Builder withClass(Class clazz, String base) {
            loaders.add(new ClassTemplateLoader(clazz, base));
            return this;
        }

        public Builder withHollowDirectory(URL dir) throws ResolverException {
            return withThis(() -> withHollowDirectory(Paths.get(dir.toURI())));
        }

        public Builder withHollowDirectory(URI dir) throws ResolverException {
            return withThis(() -> loaders.add(new HollowTemplateReader(HollowReader.load(Paths.get(dir)))));
        }

        public Builder withHollowDirectory(Path dir) {
            loaders.add(new HollowTemplateReader(HollowReader.load(dir)));
            return this;
        }

        public Resolver build() {
            cfg.setTemplateLoader(new MultiTemplateLoader(loaders.toArray(new TemplateLoader[0])));
            return new Resolver(cfg);
        }

        private Builder withThis(Exceptional runner) throws ResolverException {
            try {
                runner.run();
            } catch(Exception e) {
                if(e instanceof ResolverException) {
                    throw (ResolverException)e;
                }
                throw new ResolverException(e);
            }
            return this;
        }
    }

    private interface Exceptional {
        void run() throws Exception;
    }


}
