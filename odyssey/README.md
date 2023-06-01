# Starling Odyssey

Odyssey is a web server designed to serve up data from local Hollow data sets, with or without template-based transformations.

No Hollow schema or type information is required. Primary schemas and keys are extracted from the data sets. Primary key values of type String, Long, Integer, Double, Float and Boolean are supported.

## Config

System property `hollowPaths` or environment variable `HOLLOW_PATHS` can be set to a comma-delimited list of directories to be scanned for Hollow snapshot files. Each directory will be scanned recursively for additional directories.

## TODO

- Add Freemarker UI
  - list data sets, along with primary key/type info
  - list keys, schemas, values (paginated...)
- Add Freemarker templates for data transforms
- Possibly make this standalone, not part of Starling, so it is easier to release and leverage
- Possibly separate out odyssey-core from odyssey app so that the core can be embedded in other apps more easily?
- How to publish to maven central?