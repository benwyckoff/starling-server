package com.dragontreesoftware.odyssey.templates.freemarker;

import com.dragontreesoftware.odyssey.core.HollowReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class HollowTemplateReader implements TemplateLoader {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HollowReader hollowReader;
    private final long modDate = System.currentTimeMillis();

    public HollowTemplateReader(HollowReader hollowReader) {
        this.hollowReader = hollowReader;
    }

    @Override
    public Object findTemplateSource(String s) {
        return hollowReader.getRecordAsJsonFromString(s);
    }

    @Override
    public long getLastModified(Object o) {
        return modDate;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        String asJson = templateSource.toString();
        if (asJson != null) {
            // in order to be used as a freemarker template, the hollow record must
            // include a "body" String field
            try {
                JsonNode node = OBJECT_MAPPER.reader().readTree(asJson);
                return new StringReader(node.get("body").get("value").asText());
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        return null;
    }

    @Override
    public void closeTemplateSource(Object o) {
        // nothing to do
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("{HollowTemplateReader");
        b.append(" path:").append(hollowReader.getHollowPath().toString());
        b.append(" primaryType:").append(hollowReader.getPrimaryType());
        b.append("}");
        return b.toString();
    }
}
