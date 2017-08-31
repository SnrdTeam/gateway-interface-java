package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.contracts.dto.security.AccessServiceTokensDTO;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeException;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.SyncFailsafe;
import okhttp3.Request;
import okhttp3.Response;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Клиент Шлюза с авторизацией с использованием токена доступа и токена обновления.
 * Обновление токена доступа осуществляется автоматически.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AccessRefreshGatewayClient extends GatewayClient<AccessServiceState> implements Closeable {

    private final String _accessSchemeWord;
    private final String _refreshTokenUri;
    private final String _serviceSchemeWord;

    private final StateHandler _stateHandler;
    private final Thread _tokenRefreshThread = new Thread(new Runnable() {

        @Override
        public void run() {
            final Object idleMonitor = new Object();

            final RetryPolicy retryPolicy = new RetryPolicy()
                    .abortOn(failure -> failure instanceof RequestException && ((RequestException) failure).getCode() == 401)
                    .withBackoff(1, 72, TimeUnit.SECONDS, 1.5)
                    .withJitter(0.1);
            final SyncFailsafe failuresHandler = Failsafe
                    .with(retryPolicy)
                    // неудачная попытка обновления токенов
                    .onFailedAttempt(failure -> {
                        if (_stateHandler != null)
                            _stateHandler.onTokenRefreshFailedAttempt(failure);
                    });

            while (!Thread.currentThread().isInterrupted()) {

                long now = now();

                try {
                    failuresHandler.run(() -> {
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
                    });
                } catch (FailsafeException e) {
                    // фатальная ошибка обновления токенов - дальнейшие попытки обновления токенов бессмысленны

                    if (_stateHandler != null)
                        _stateHandler.onAccessLost();
                    break;
                }

                try {
                    synchronized (idleMonitor) {
                        idleMonitor.wait((_state.getAccessTokenValidTo() - now) / 2);
                    }
                } catch (InterruptedException ignore) {
                    break;
                }
            }
        }
    }, "Token refresh thread");

    /**
     * Создание экземпляра класса {@link AccessRefreshGatewayClient}
     *
     * @param gatewayUrl        Адрес Шлюза
     * @param state             Состояние Агента
     * @param refreshTokenUri   Путь запроса обновления токена доступа
     * @param accessSchemeWord  Имя схемы авторизации для доступа к шлюзу
     * @param serviceSchemeWord Имя схемы авторизации для доступа к шлюзу с целью выполнения служебных операций
     * @param stateHandler      Обработчик изменения состояния клиента Шлюза
     */
    protected AccessRefreshGatewayClient(URL gatewayUrl,
                                         AccessServiceState state,
                                         String refreshTokenUri,
                                         String accessSchemeWord,
                                         String serviceSchemeWord,
                                         StateHandler stateHandler) {

        super(gatewayUrl, state, stateHandler, AccessServiceState.class);

        if (state == null)
            throw new NullPointerException("state cannot be null");

        _refreshTokenUri = refreshTokenUri;
        _accessSchemeWord = accessSchemeWord;
        _serviceSchemeWord = serviceSchemeWord;

        _stateHandler = stateHandler;
        _tokenRefreshThread.start();
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        _tokenRefreshThread.interrupt();
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

    private Request.Builder createAuthorizedRequestBuilder(String requestUri, String schemeWord, String token)
            throws MalformedURLException {

        return createRequestBuilder(requestUri)
                .addHeader("Authorization", schemeWord + " " + token);
    }

    /**
     * Обработчик изменения состояния клиента Шлюза
     */
    public interface StateHandler extends GatewayClient.StateHandler<AccessServiceState> {

        /**
         * Ошибка при попытке обновления токенов. После данной ошибки будет произведена повторная попытка обновления токенов
         *
         * @param failure Причина ошибки обновления токенов
         */
        void onTokenRefreshFailedAttempt(Throwable failure);

        /**
         * Ошибка невозможности получения доступа к Шлюзу. Возникает при истечении сроков жизни токена доступа и токена обновления.
         * Дальнейшие запросы с использованием данного клиента будут приводить к ошибке 401.
         */
        void onAccessLost();
    }
}
