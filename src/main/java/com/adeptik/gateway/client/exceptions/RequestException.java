package com.adeptik.gateway.client.exceptions;

public class RequestException extends Exception {

    private final int _code;
    private final String _body;

    public RequestException(int code, String body) {
        super("Request error: code " + code);
        _code = code;
        _body = body;
    }

    public int getCode() {
        return _code;
    }

    public String getBody() {
        return _body;
    }
}
