package com.adeptik.gateway.client.utils;

import okhttp3.MediaType;

/**
 * Типы медиа
 */
public final class MediaTypes {

    /**
     * JSON
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * octet stream
     */
    public static final MediaType OctetStream = MediaType.parse("application/octet-stream");
}
