package com.adeptik.gateway.client.utils;

import java.io.IOException;
import java.io.InputStream;

public interface StreamHandler {

    void handle(InputStream inputStream)
            throws IOException;
}
