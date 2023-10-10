package com.dragontreesoftware.odyssey.service;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemAnnouncementWatcher;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.stringifier.HollowRecordJsonStringifier;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class HollowReader<T> {

    private final Path hollowPath;

    // allow configuration
    private final HollowRecordJsonStringifier stringifier = new HollowRecordJsonStringifier(true, false);

    private final HollowFilesystemBlobRetriever blobRetriever;
    private final HollowFilesystemAnnouncementWatcher announcementWatcher;
    private final HollowConsumer consumer;
    private PrimaryKey primaryKey;
    private HollowObjectSchema primarySchema;

    private HollowPrimaryKeyIndex primaryKeyIndex;
    private String primaryType;

    private String keyType;

    private Function<String,T> converter;

    // maybe have untyped HollowReader, and then a TypedHollowReader wrapper that adds the get(T) method

    public static HollowReader load(Path hollowPath) {
        File file = hollowPath.toFile();
        if(file.exists() && file.isDirectory() && file.list((dir, name) -> name.toLowerCase().startsWith("snapshot")).length > 0) {
            HollowReader untyped = new HollowReader(hollowPath);
            String keyType = untyped.getKeyType();
            if(keyType == null) {
                return untyped;
            }
            return switch(keyType) {
                case "String" -> new HollowReader<String>(untyped).withConverter(v -> v);
                case "Integer" -> new HollowReader<Integer>(untyped).withConverter(Integer::parseInt);
                case "Long" -> new HollowReader<Long>(untyped).withConverter(Long::parseLong);
                case "Float" -> new HollowReader<Float>(untyped).withConverter(Float::parseFloat);
                case "Double" -> new HollowReader<Double>(untyped).withConverter(Double::parseDouble);
                case "Boolean" -> new HollowReader<Boolean>(untyped).withConverter(Boolean::valueOf);
                default -> null;
            };
        }
        return null;
    }

    private HollowReader(HollowReader untyped) {
        this.hollowPath = untyped.hollowPath;
        this.primaryKey = untyped.primaryKey;
        this.primaryKeyIndex = untyped.primaryKeyIndex;
        this.primarySchema = untyped.primarySchema;
        this.blobRetriever = untyped.blobRetriever;
        this.announcementWatcher = untyped.announcementWatcher;
        this.consumer = untyped.consumer;
        this.keyType = untyped.keyType;
        this.primaryType = untyped.primaryType;
        this.converter = untyped.converter;
    }

    private HollowReader(Path hollowPath) {
        this.hollowPath = hollowPath;
        blobRetriever =
                new HollowFilesystemBlobRetriever(hollowPath);
        announcementWatcher =
                new HollowFilesystemAnnouncementWatcher(hollowPath);

        consumer = HollowConsumer.withBlobRetriever(blobRetriever)
                .withAnnouncementWatcher(announcementWatcher)
                .build();

        consumer.triggerRefresh();

        Optional<HollowSchema> primarySchema = consumer.getStateEngine().getSchemas().stream().filter(s -> s.getSchemaType() == HollowSchema.SchemaType.OBJECT)
                .filter(s -> ((HollowObjectSchema) s).getPrimaryKey() != null).findFirst();
        if (primarySchema.isPresent()) {
            this.primarySchema = ((HollowObjectSchema) primarySchema.get());
            primaryKey = this.primarySchema.getPrimaryKey();
            primaryKeyIndex = new HollowPrimaryKeyIndex(consumer.getStateEngine(), primaryKey);
            primaryType = primaryKey.getType();
        }

        getRecognizedHollowPrimaryKeyType(); // for side effect
        //consumer.getStateEngine().getSchemas().stream().forEach(System.out::println);
    }

    public HollowReader<T> withConverter(Function<String,T> converter) {
        this.converter = converter;
        return this;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public Class getRecognizedHollowPrimaryKeyType() {
        if(primaryKey != null) {
            HollowObjectSchema.FieldType fieldType = primaryKey.getFieldType(consumer.getStateEngine(), primaryKey.getFieldPaths().length-1);

            Class type = switch(fieldType) {
                case REFERENCE,BYTES -> null;
                case INT -> Integer.class;
                case LONG -> Long.class;
                case BOOLEAN -> Boolean.class;
                case FLOAT -> Float.class;
                case DOUBLE -> Double.class;
                case STRING -> String.class;
            };
            if(type != null) {
                this.keyType = type.getSimpleName();
            }
            return type;
        }
        return null;
    }

    public List<String> getPrimaryKeys() {

        List<String> keys = new LinkedList<>();

        if(primaryType != null) {
            HashMap<String, Integer> typedOrdinals = consumer.getMetrics().getTypePopulatedOrdinals();
            if (typedOrdinals != null) {
                Integer count = typedOrdinals.get(primaryType);
                if (count != null) {
                    StringBuilder b = new StringBuilder();
                    for (int o = 0; o < count; o++) {
                        Object[] key = primaryKeyIndex.getRecordKey(o);
                        b.setLength(0);
                        for (Object k : key) {
                            b.append(k.toString());
                        }
                        keys.add(b.toString());
                    }
                }
            }
        }
        return keys;
    }

    public T convertKey(String id) {
        return converter.apply(id);
    }

    public HollowRecord getRecordFromString(String id) {
        return getRecord(convertKey(id));
    }

    public HollowRecord getRecord(T id) {
        if (primaryKeyIndex != null) {
            int ordinal = primaryKeyIndex.getMatchingOrdinal(id);
            if(ordinal >= 0) {
                GenericHollowObject kit = new GenericHollowObject(consumer.getStateEngine(), primaryType, ordinal);
                return kit;
            }
        }
        return null;
    }

    public String getRecordAsJsonFromString(String id) {
        return getRecordAsJson(convertKey(id));
    }

    public String getRecordAsJson(T id) {
        HollowRecord rec = getRecord(id);
        if(rec != null) {
            return stringifier.stringify(rec);
        }
        return null;
    }

    public HollowRecord getRecordFromOrdinal(int ordinal) {
        if (primaryKeyIndex != null && ordinal >= 0) {
            GenericHollowObject kit = new GenericHollowObject(consumer.getStateEngine(), primaryType, ordinal);
            return kit;
        }
        return null;
    }

    public String getRecordAsJsonFromOrdinal(int ordinal) {
        HollowRecord rec = getRecordFromOrdinal(ordinal);
        if(rec != null) {
            return stringifier.stringify(rec);
        }
        return null;
    }


    public String getPrimaryType() {
        return primaryType;
    }
}
