package com.adeptik.gateway.client.model;

import com.adeptik.gateway.contracts.dto.security.AccessRefreshTokensDTO;

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

    public static AccessServiceState from(AccessRefreshTokensDTO tokens, long now) {

        if (tokens == null)
            throw new NullPointerException("tokens cannot be null");

        AccessServiceState state = new AccessServiceState();
        state.setAccessToken(tokens.Access.Token);
        state.setAccessTokenValidTo(now + tokens.Access.ExpiresIn);
        state.setServiceToken(tokens.Service.Token);
        state.setServiceTokenValidTo(now + tokens.Service.ExpiresIn);
        return state;
    }
}
