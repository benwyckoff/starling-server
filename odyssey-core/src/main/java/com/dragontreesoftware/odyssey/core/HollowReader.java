package com.dragontreesoftware.odyssey.core;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.stringifier.HollowRecordJsonStringifier;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class HollowReader<T> implements ReferencedObject {

    private final ReferenceTracker referenceTracker = new ReferenceTracker();
    private final Path hollowPath;

    // allow configuration
    private final HollowRecordJsonStringifier stringifier = new HollowRecordJsonStringifier(true, false);

    private final HollowStorageFactory storageFactory;
    private final HollowReaderKey hollowReaderKey;
    private HollowConsumer.BlobRetriever blobRetriever;
    private HollowConsumer.AnnouncementWatcher announcementWatcher;
    private HollowConsumer consumer;
    private PrimaryKey primaryKey;
    private HollowObjectSchema primarySchema;

    private HollowPrimaryKeyIndex primaryKeyIndex;
    private String primaryType;

    private String keyType;

    private Function<String,T> converter;

    public static HollowReader load(Path hollowPath) {
        return load(hollowPath, HollowFileStorageFactory.INSTANCE);
    }

    public static HollowReader load(Path hollowPath, HollowStorageFactory factory) {
        if(factory.validPath(hollowPath)) {
            HollowReader untyped = new HollowReader(hollowPath, factory);
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
                default -> new HollowReader<List<Object>>(untyped).withConverter(HollowReader.convertCompound(keyType));
            };
        }
        return null;
    }

    private static Function<String, List<Object>> convertCompound(String v) {
        List<Function<String,Object>> converters = new ArrayList<>();

        List<String> types = HollowKeyType.parse(v);

        for(String type : types) {
            Function<String, Object> converter = switch (type) {
                case "String" -> val -> val;
                case "Integer" -> Integer::parseInt;
                case "Long" -> Long::parseLong;
                case "Float" -> Float::parseFloat;
                case "Double" -> Double::parseDouble;
                case "Boolean" -> Boolean::valueOf;
                default -> val -> val;
            };
            converters.add(converter);
        }

        return value -> {
            List<Object> resultList = new LinkedList<>();
            List<String> parts = HollowKeyType.parse(value);
            for(int i = 0; i < parts.size(); i++) {
                resultList.add(converters.get(i).apply(parts.get(i)));
            }
            return resultList;
        };
    }

    public Path getHollowPath() {
        return hollowPath;
    }

    public boolean isClosed() {
        return consumer == null;
    }

    public boolean isOpen() {
        return consumer != null;
    }

    public void close() {
        // TODO figure out if this actually frees up resources. I don't see "close" methods on these things
        if(isClosed()) {
            return;
        }
        blobRetriever = null;
        announcementWatcher = null;
        consumer = null;
    }

    public void open() {
        touch();
        if(isOpen()) {
            return;
        }
        blobRetriever = storageFactory.createRetriever(hollowPath);
        announcementWatcher = storageFactory.createAnnouncementWatcher(hollowPath);

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

        getRecognizedHollowPrimaryKeyType();
        touch();
    }


    public HollowReaderKey getHollowReaderKey() {
        return hollowReaderKey;
    }

    public HollowConsumerMetrics getMetrics() {
        open();
        if(isOpen()) {
            return consumer.getMetrics();
        }
        return null;
    }

    public List<HollowSchema> getSchemas() {
        open();
        if(isOpen()) {
            return consumer.getStateEngine().getSchemas();
        }
        return null;
    }

    private HollowReader(HollowReader untyped) {
        this.hollowPath = untyped.hollowPath;
        this.storageFactory = untyped.storageFactory;
        this.primaryKey = untyped.primaryKey;
        this.primaryKeyIndex = untyped.primaryKeyIndex;
        this.primarySchema = untyped.primarySchema;
        this.blobRetriever = untyped.blobRetriever;
        this.announcementWatcher = untyped.announcementWatcher;
        this.consumer = untyped.consumer;
        this.keyType = untyped.keyType;
        this.primaryType = untyped.primaryType;
        this.converter = untyped.converter;
        this.hollowReaderKey = untyped.hollowReaderKey;
    }

    private HollowReader(Path hollowPath, HollowStorageFactory factory) {
        this.hollowPath = hollowPath;
        this.storageFactory = factory;
        open();
        this.hollowReaderKey = new HollowReaderKey(this.hollowPath.toString(), this.primaryType);
    }

    public HollowReader<T> withConverter(Function<String,T> converter) {
        this.converter = converter;
        return this;
    }

    public String getKeyType() {
        touch();
        if(isClosed()) {
            open();
        }
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }



    public void getRecognizedHollowPrimaryKeyType() {
        touch();
        if(isClosed()) {
            open();
        }
        if(primaryKey != null) {
            HollowKeyType.Builder builder = new HollowKeyType.Builder();

            for(int i = 0; i < primaryKey.getFieldPaths().length; i++) {

                HollowObjectSchema.FieldType fieldType = primaryKey.getFieldType(consumer.getStateEngine(), i);

                Class type = switch (fieldType) {
                    case REFERENCE, BYTES -> null;
                    case INT -> Integer.class;
                    case LONG -> Long.class;
                    case BOOLEAN -> Boolean.class;
                    case FLOAT -> Float.class;
                    case DOUBLE -> Double.class;
                    case STRING -> String.class;
                };
                if(type != null) {
                    builder.with(type.getSimpleName());
                }
            }
            this.keyType = builder.build().getName();
        }
    }

    public List<String> getPrimaryKeys() {
        return getPrimaryKeys(0, Integer.MAX_VALUE);
    }

    public List<String> getPrimaryKeys(int from, int numKeys) {
        touch();
        if(isClosed()) {
            open();
        }

        List<String> keys = new ArrayList<>();

        if(primaryType != null) {
            HashMap<String, Integer> typedOrdinals = consumer.getMetrics().getTypePopulatedOrdinals();
            if (typedOrdinals != null) {
                Integer count = typedOrdinals.get(primaryType);
                if (count != null) {
                    BitSet populatedOrdinals = consumer.getStateEngine().getTypeState(primaryType).getPopulatedOrdinals();
                    populatedOrdinals.stream().skip(from).limit(numKeys).forEach(pi -> {
                        Object[] key = primaryKeyIndex.getRecordKey(pi);
                        HollowKeyType.Builder b = new HollowKeyType.Builder();
                        for (Object k : key) {
                            b.with(k.toString());
                        }
                        keys.add(b.build().getName());
                    });
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
        touch();
        if(isClosed()) {
            open();
        }
        if (primaryKeyIndex != null) {
            int ordinal = -1;
            if(id instanceof List) {
                ordinal = primaryKeyIndex.getMatchingOrdinal(((List)id).toArray(new Object[0]));
            } else {
                ordinal = primaryKeyIndex.getMatchingOrdinal(id);
            }
            if(ordinal >= 0) {
                return new GenericHollowObject(consumer.getStateEngine(), primaryType, ordinal);
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
        touch();
        if(isClosed()) {
            open();
        }
        if (primaryKeyIndex != null && ordinal >= 0) {
            return new GenericHollowObject(consumer.getStateEngine(), primaryType, ordinal);
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
        touch();
        if(isClosed()) {
            open();
        }
        return primaryType;
    }

    @Override
    public void touch() {
        referenceTracker.touch();
    }

    @Override
    public long getReferenceCount() {
        return referenceTracker.getReferenceCount();
    }

    @Override
    public long getLastReferenceTimestamp() {
        return referenceTracker.getLastReferenceTimestamp();
    }

    @Override
    public long idleTime(long now) {
        return referenceTracker.idleTime(now);
    }

    @Override
    public long idle() {
        return referenceTracker.idle();
    }
}
