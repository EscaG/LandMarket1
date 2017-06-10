package com.example.esca.landmarket.httpRequest;

public interface IResponse {
    boolean isError();

    String getBody();

    int getStatus();
}