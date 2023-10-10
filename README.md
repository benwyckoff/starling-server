# starling-server

Starling-server is a collection of web servers to be used as test back-ends for load generation or mocking.

## jdog

jdog is a Java Dynamic Object Generator web server. The response it generates is based on the request received.

## odyssey

odyssey is a web server designed to serve the contents of Netflix Hollow data sets, with or without transformation through a templating engine.

### TODO

- make odyssey a library so it can be loaded into other apps
- possibly then make odyssey-lib vs odyssey-server

## jdog-grpc-interface

interface definition for a grpc interface to the JDog service

## TODO

- compile .proto
- add GrpcService to jdog as an alternate "controller" fronting jdog
- extend proto to include more dog request options and response data
- 