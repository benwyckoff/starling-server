package com.dragontreesoftware.jdog.http.web;

import com.dragontreesoftware.jdog.core.ObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@RequestMapping(value = "/")
public class JDogController {

    @Autowired
    private JDogService jDogService;
    private final AtomicLong requestCounter = new AtomicLong();

    public JDogController() {
        System.out.println("Hello World");
    }

    @GetMapping(value = "/dog/**")
    public ResponseEntity<?> get(HttpServletRequest request) throws IOException {
        requestCounter.incrementAndGet();
        ObjectResponse objectResponse = jDogService.get(buildOptions(request));
        switch (objectResponse.getType()) {
            case PLAIN_TEXT:
                return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(new String(objectResponse.getBody(), StandardCharsets.UTF_8));
            case JSON:
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).contentLength(objectResponse.getSize()).body(new String(objectResponse.getBody(), StandardCharsets.UTF_8));
            case PNG:
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(objectResponse.getBody());
            case BINARY:
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(objectResponse.getBody());
            default:
                return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value="/stats")
    public ResponseEntity<?> stats(HttpServletRequest request) {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(String.format("request count: %d", this.requestCounter.get()));
    }

    /**
     * build an options map by first looking at headers, then the uri path, then query params.
     *
     * @param request
     * @return an options map
     */
    Map<String, String> buildOptions(HttpServletRequest request) {
        Map<String, String> options = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(name -> options.put(name, request.getHeader(name)));
        String uri = request.getRequestURI();
        if (uri.startsWith("/dog")) {
            uri = uri.substring(4);
        }
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        if (!uri.isBlank()) {
            String[] tokens = uri.split("/");
            for (int i = 0; i < tokens.length / 2; i++) {
                options.put(tokens[i * 2], tokens[1 + i * 2]);
            }
        }
        // take LAST parameter for each key, in case there are multiples
        request.getParameterMap().forEach((key, values) -> options.put(key, values[values.length - 1]));
        return options;
    }
}
