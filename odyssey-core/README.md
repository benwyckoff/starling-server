# Starling Odyssey

Odyssey is a collection of modules designed to serve up data from local (or GCS) Hollow data sets, with or without template-based transformations.

No Hollow schema or type information is required. Primary schemas and keys are extracted from the data sets. Primary key values of type String, Long, Integer, Double, Float and Boolean are supported.

Odyssey serves as a "hollow explorer" as well as a possible load/mock server.

## odyssey-core

Odyssey-core is the main module that provides functionality for loading and caching hollow data sets in memory. There is also support for an idle timer that will close idle data sets to reduce memory consumption. The framework is extensible and supports a number of factory types to allow for e.g. hollow data sets to be read or loaded from other locations.

- call HollowReaders.setIdleListener to get notified when readers are closed. They are not automatically removed from the HollowReaders map so they can be re-opened, but an implementation may want to clean them up and provide alternate means of discovery. 


### Sample Data Sets

The test code for odyssey-core includes DataSetGenerator and DataSetGenerator2 that produce local sample data sets for testing, and as reference code for generating your own local data sets.

## odyssey-gcs

Odyssey-gcs is a module that provides factories for reading hollow data sets from GCS buckets. There are certain assumptions about data set naming conventions that may not match your specific use case.

## odyssey-server

The odyssey-server is a SpringBoot 3 server that has a controller and service that map to the odyssey-core and optionally odyssey-gcs functionality.

The HollowTypeReadStateMixin class in the server prevents the HollowReadState in the schema from being serialized. If it is not excluded, there would be an infinite loop - the loop is aborted, but the resulting output is partially broken.

### Config

System property `hollowPaths` or environment variable `HOLLOW_PATHS` can be set to a comma-delimited list of local directories to be scanned for Hollow snapshot files. Each directory will be scanned recursively for additional directories.

#### TODO

Describe the spring boot server options 

### Managing Multiple Data Sets

Since Odyssey can load multiple hollow data sets at a time, users must identify the data set in question as well as any primary keys etc. A HollowReaderKey includes the path to the hollow data set, a Java hash code of that path, and the type of the primary key. Any one of these may be used to identify the data set, but if there are collisions (e.g. on the primary key type if multiple snapshots of the same basic data set are loaded) an error is returned. There will not be collisions on the full path, but it's awkward to use. The idHash is likely the safest option, and is consistent from run to run.

### Sample Urls

- http://localhost:8080/hollow/types
- http://localhost:8080/hollow/keys/Kit
- http://localhost:8080/hollow/keys/Team
- http://localhost:8080/hollow/Team/Team-100
- http://localhost:8080/hollow/Kit/kit-101
- http://localhost:8080/hollow/metrics/Team
- http://localhost:8080/hollow/schema/Team
- http://localhost:8080/hollow/schema/Team/string
- http://localhost:8080/hollow/ordinal/Team/2
- http://localhost:8080/hollow/keys/Kit/0/5
- http://localhost:8080/hollow/keys/Kit/5/5
- http://localhost:8080/hollow/keys/-1821776703
- http://localhost:8080/hollow/-1821776703/kit-101
- http://localhost:8080/hollow/schema/-1821776703
