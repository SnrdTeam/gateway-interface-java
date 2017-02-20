package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.contracts.dto.security.AccessRefreshTokensDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("WeakerAccess")
public abstract class AccessRefreshGatewayClient extends GatewayClient<AccessServiceState> implements Closeable {

    private final String _accessSchemeWord;
    private final String _refreshTokenUri;
    private final String _serviceSchemeWord;

    private final Timer _refreshTimer = new Timer("Token Refresh Timer");
    private final TimerTask _refreshTimerTask = new TimerTask() {

        @Override
        public void run() {
            refreshToken();
        }
    };

    protected AccessRefreshGatewayClient(URL gatewayUrl,
                                         AccessServiceState state,
                                         String refreshTokenUri,
                                         String accessSchemeWord,
                                         String serviceSchemeWord)
            throws InstantiationException, IllegalAccessException {

        super(gatewayUrl, state, AccessServiceState.class);

        if (state == null)
            throw new NullPointerException("state cannot be null");

        _refreshTokenUri = refreshTokenUri;
        _accessSchemeWord = accessSchemeWord;
        _serviceSchemeWord = serviceSchemeWord;

        refreshToken();
    }

    @Override
    public void close() throws IOException {
        _refreshTimer.cancel();
        _refreshTimer.purge();
    }

    protected Request.Builder createAuthorizedRequestBuilder(String requestUri)
            throws MalformedURLException {

        return createAuthorizedRequestBuilder(requestUri, _accessSchemeWord, _state.getAccessToken());
    }

    private synchronized void refreshToken() {

        long now = now();

        try {
            Request request = createAuthorizedRequestBuilder(_refreshTokenUri, _serviceSchemeWord, _state.getServiceToken())
                    .build();
            try (Response response = createHttpClient().newCall(request).execute()) {

                AccessRefreshTokensDTO tokens = readJsonResponse(response, AccessRefreshTokensDTO.class);
                synchronized (_state) {
                    _state.setAccessToken(tokens.Access.Token);
                    _state.setAccessTokenValidTo(now + tokens.Access.ExpiresIn);
                    _state.setServiceToken(tokens.Service.Token);
                    _state.setServiceTokenValidTo(now + tokens.Service.ExpiresIn);
                }
            }
        } catch (IOException | RequestException e) {
            throw new RuntimeException(e);
        }

        _refreshTimer.schedule(_refreshTimerTask, Math.max(500, (_state.getAccessTokenValidTo() - now) / 2));
    }

    private Request.Builder createAuthorizedRequestBuilder(String requestUri, String schemeWord, String token)
            throws MalformedURLException {

        return createRequestBuilder(requestUri)
                .addHeader("Authorization", schemeWord + " " + token);
    }
}
