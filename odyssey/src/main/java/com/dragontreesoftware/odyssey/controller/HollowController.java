package com.dragontreesoftware.odyssey.controller;

import com.dragontreesoftware.odyssey.service.HollowReaderService;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.core.schema.HollowSchema;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class HollowController {

    private final HollowReaderService hollowReaderService;

    public HollowController(@Autowired HollowReaderService hollowReaderService) {
        this.hollowReaderService = hollowReaderService;
    }

    @GetMapping(value = "/hollow/{type:.+}/{id:.+}")
    public ResponseEntity<?> getType(HttpServletRequest request, @PathVariable String type, @PathVariable String id) {
        String record = hollowReaderService.get(type, id);
        if(record != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).contentLength(record.length())
                    .body(record);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/hollow/types")
    public ResponseEntity<?> getTypes(HttpServletRequest request) {
        List<String> types = hollowReaderService.getTypes();
        if(types != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(types);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping(value = "/hollow/metrics/{type:.+}")
    public ResponseEntity<?> getMetrics(HttpServletRequest request, @PathVariable String type) {
        Optional<HollowConsumerMetrics> metrics = hollowReaderService.getMetrics(type);
        if (metrics.isPresent()) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(metrics.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/hollow/schema/{type:.+}")
    public ResponseEntity<?> getSchema(HttpServletRequest request, @PathVariable String type) {

        // HollowTypeReadStateMixin prevents the HollowTypeReadState from being serialized
        // in the JSON response, because they create infinite recursion
        Optional<List<HollowSchema>> schemas = hollowReaderService.getSchemas(type);
        if (schemas.isPresent()) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(schemas.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/hollow/schema/{type:.+}/string")
    public ResponseEntity<?> getSchemaStrings(HttpServletRequest request, @PathVariable String type) {

        Optional<List<HollowSchema>> schemas = hollowReaderService.getSchemas(type);
        if (schemas.isPresent()) {
            List<String> asStrings = schemas.get().stream().map(HollowSchema::toString).toList();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(asStrings);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }



    @GetMapping(value = "/hollow/keys/{type:.+}/{from:.+}/{count:.+}")
    public ResponseEntity<?> getKeysRange(HttpServletRequest request,
                                          @PathVariable String type,
                                          @PathVariable Integer from,
                                          @PathVariable Integer count) {
        List<String> keys = hollowReaderService.getTypedKeys(type, from, count);
        if(keys != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(keys);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/hollow/keys/{type:.+}")
    public ResponseEntity<?> getKeys(HttpServletRequest request, @PathVariable String type) {
        List<String> keys = hollowReaderService.getTypedKeys(type);
        if(keys != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(keys);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping(value = "/hollow/ordinal/{type:.+}/{id:.+}")
    public ResponseEntity<?> getOrdinal(HttpServletRequest request, @PathVariable String type, @PathVariable Integer id) {
        String record = hollowReaderService.getFromOrdinal(type, id);
        if(record != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).contentLength(record.length())
                    .body(record);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
