package com.example.demo.util;

import lombok.Data;

@Data
public class ServiceProvider {
    public static enum SERVICE_PROVIDER {
        CLYDE, VISA, MASTERCARD
    }

    private String serviceProvider;
}
