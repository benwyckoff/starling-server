package com.dragontree.odyssey.controller;

import com.dragontree.odyssey.service.HollowReaderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class HollowController {

    private HollowReaderService hollowReaderService;

    public HollowController(@Autowired HollowReaderService hollowReaderService) {
        this.hollowReaderService = hollowReaderService;
    }

    @GetMapping(value = "/hollow/{id:.+}")
    public ResponseEntity<?> get(HttpServletRequest request, @PathVariable String id) throws IOException {
        String record = hollowReaderService.get(id);
        if(record != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).contentLength(record.length())
                    .body(record);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/hollow/{type:.+}/{id:.+}")
    public ResponseEntity<?> getType(HttpServletRequest request, @PathVariable String type, @PathVariable String id) throws IOException {
        String record = hollowReaderService.get(type, id);
        if(record != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).contentLength(record.length())
                    .body(record);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/hollow/types")
    public ResponseEntity<?> getTypes(HttpServletRequest request) throws IOException {
        List<String> types = hollowReaderService.getTypes();
        if(types != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(types);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/hollow/keys/{type:.+}")
    public ResponseEntity<?> getKeys(HttpServletRequest request, @PathVariable String type) throws IOException {
        List<String> keys = hollowReaderService.getTypedKeys(type);
        if(keys != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(keys);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }



    @GetMapping(value = "/hollow/ordinal/{id:.+}")
    public ResponseEntity<?> getOrdinal(HttpServletRequest request, @PathVariable Integer id) throws IOException {
        String record = hollowReaderService.getFromOrdinal(id);
        if(record != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).contentLength(record.length())
                    .body(record);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
