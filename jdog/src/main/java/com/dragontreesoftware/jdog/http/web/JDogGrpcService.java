package com.dragontreesoftware.jdog.http.web;

import com.dragontreesoftware.jdog.DogReply;
import com.dragontreesoftware.jdog.DogRequest;
import com.dragontreesoftware.jdog.JDogServiceGrpc;
import com.dragontreesoftware.jdog.core.ObjectResponse;
import com.dragontreesoftware.jdog.core.ObjectType;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@GrpcService
public class JDogGrpcService extends JDogServiceGrpc.JDogServiceImplBase {

    @Autowired
    private JDogService jDogService;

    @Override
    public void generate(DogRequest request, StreamObserver<DogReply> responseObserver) {

        String type = request.getMediaType();
        Map<String,String> options = request.getOptionsMap();
        if(!type.isBlank()) {
            options = new HashMap<>(options);
            options.put("type", type);
        } else if(!options.containsKey("type")) {
            options = new HashMap<>(options);
            options.put("type", ObjectType.PLAIN_TEXT.getCode());
        }

        ObjectResponse objectResponse;
        try {
            objectResponse = jDogService.get(options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String responseType = switch(objectResponse.getType()) {
            case PLAIN_TEXT -> MediaType.TEXT_PLAIN_VALUE;
            case JSON -> MediaType.APPLICATION_JSON_VALUE;
            case BINARY -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
            case PNG -> MediaType.IMAGE_PNG_VALUE;
        };

        DogReply.Builder replyBuilder = DogReply.newBuilder()
                .setMediaType(responseType);

        switch (objectResponse.getType()) {
            case PLAIN_TEXT, JSON -> {
                replyBuilder.setText(new String(objectResponse.getBody(), StandardCharsets.UTF_8));
                break;
            }
            case BINARY, PNG -> {
                replyBuilder.setBinary(ByteString.copyFrom(objectResponse.getBody()));
                break;
            }
        }

        DogReply reply = replyBuilder.build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
