package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.contracts.dto.security.AccessServiceTokensDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Клиент Шлюза с авторизацией с использованием токена доступа и токена обновления.
 * Обновление токена доступа осуществляется автоматически.
 */
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

    /**
     * Создание экземпляра класса {@link AccessRefreshGatewayClient}
     *
     * @param gatewayUrl        Адрес Шлюза
     * @param state             Состояние Агента
     * @param refreshTokenUri   Путь запроса обновления токена доступа
     * @param accessSchemeWord  Имя схемы авторизации для доступа к шлюзу
     * @param serviceSchemeWord Имя схемы авторизации для доступа к шлюзу с целью выполнения служебных операций
     */
    protected AccessRefreshGatewayClient(URL gatewayUrl,
                                         AccessServiceState state,
                                         String refreshTokenUri,
                                         String accessSchemeWord,
                                         String serviceSchemeWord) {

        super(gatewayUrl, state, AccessServiceState.class);

        if (state == null)
            throw new NullPointerException("state cannot be null");

        _refreshTokenUri = refreshTokenUri;
        _accessSchemeWord = accessSchemeWord;
        _serviceSchemeWord = serviceSchemeWord;

        refreshToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        _refreshTimer.cancel();
        _refreshTimer.purge();
    }

    /**
     * Создание построителя HTTP-запроса, в который добавлены необходимые заголовки авторизации
     *
     * @param requestUri Путь к методу HTTP
     * @return Построитель HTTP-запроса
     * @throws MalformedURLException Некорректный путь к методу HTTp
     */
    protected Request.Builder createAuthorizedRequestBuilder(String requestUri)
            throws MalformedURLException {

        return createAuthorizedRequestBuilder(requestUri, _accessSchemeWord, _state.getAccessToken());
    }

    private synchronized void refreshToken() {

        long now = now();

        try {
            Request request = createAuthorizedRequestBuilder(_refreshTokenUri, _serviceSchemeWord, _state.getServiceToken())
                    .post(createEmptyRequestBody())
                    .build();
            try (Response response = createHttpClient().newCall(request).execute()) {

                AccessServiceTokensDTO tokens = readJsonResponse(response, AccessServiceTokensDTO.class);
                synchronized (_state) {
                    _state.setAccessToken(tokens.Access.Token);
                    _state.setAccessTokenValidTo(now + tokens.Access.ExpiresIn);
                    _state.setServiceToken(tokens.Service.Token);
                    _state.setServiceTokenValidTo(now + tokens.Service.ExpiresIn);
                }

                onStateChanged();
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
