package com.adeptik.gateway.client.model;

public class AccessState {

    private String _accessToken;
    private long _accessTokenValidTo;

    public String getAccessToken() {
        return _accessToken;
    }

    public void setAccessToken(String accessToken) {
        this._accessToken = accessToken;
    }

    public long getAccessTokenValidTo() {
        return _accessTokenValidTo;
    }

    public void setAccessTokenValidTo(long accessTokenValidTo) {
        this._accessTokenValidTo = accessTokenValidTo;
    }
}
