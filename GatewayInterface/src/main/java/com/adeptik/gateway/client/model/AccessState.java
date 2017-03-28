package com.adeptik.gateway.client.model;

/**
 * Состояние клиента Шлюза
 */
public class AccessState {

    private String _accessToken;
    private long _accessTokenValidTo;

    /**
     * Получение токена доступа
     *
     * @return Токен доступа
     */
    public String getAccessToken() {
        return _accessToken;
    }

    /**
     * Установка токена доступа
     *
     * @param accessToken Токен доступа
     */
    public void setAccessToken(String accessToken) {
        this._accessToken = accessToken;
    }

    /**
     * Получение срока действия токена доступа в UTC в милисекундах от "эры UNIX" (01.01.1970 00:00:00.0)
     *
     * @return Срок действия токена доступа в UTC в милисекундах
     */
    public long getAccessTokenValidTo() {
        return _accessTokenValidTo;
    }

    /**
     * Установка срока действия токена доступа в UTC в милисекундах от "эры UNIX" (01.01.1970 00:00:00.0)
     *
     * @param accessTokenValidTo Срок действия токена доступа в UTC в милисекундах
     */
    public void setAccessTokenValidTo(long accessTokenValidTo) {
        this._accessTokenValidTo = accessTokenValidTo;
    }
}
