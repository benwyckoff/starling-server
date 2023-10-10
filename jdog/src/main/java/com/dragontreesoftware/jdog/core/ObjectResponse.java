package com.dragontreesoftware.jdog.core;

public class ObjectResponse {

    private ResponseType type;
    private int size;

    private byte[] body;

    public ObjectResponse(ResponseType type) {
        this.type = type;
    }

    public ObjectResponse(ResponseType type, byte[] body) {
        this.type = type;
        this.body = body;
        this.size = body.length;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
