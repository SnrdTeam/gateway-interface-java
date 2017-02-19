package com.adeptik.gateway.client.model;

public class AccessServiceState extends AccessState {

    private String _serviceToken;
    private long _serviceTokenValidTo;

    public String getServiceToken() {
        return _serviceToken;
    }

    public void setServiceToken(String serviceToken) {
        this._serviceToken = serviceToken;
    }

    public long getServiceTokenValidTo() {
        return _serviceTokenValidTo;
    }

    public void setServiceTokenValidTo(long serviceTokenValidTo) {
        this._serviceTokenValidTo = serviceTokenValidTo;
    }
}
