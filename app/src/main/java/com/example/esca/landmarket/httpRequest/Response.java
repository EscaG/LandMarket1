package com.example.esca.landmarket.httpRequest;

public class Response implements IResponse {
    int status;
    String body;

    public Response(int responseCode, String responseBody) {
        status = responseCode;
        body = responseBody;
    }

    @Override
    public boolean isError() {
        return status >= 300;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public int getStatus() {
        return status;
    }
}