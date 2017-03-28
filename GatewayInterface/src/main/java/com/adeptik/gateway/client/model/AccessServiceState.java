package com.adeptik.gateway.client.model;

import com.adeptik.gateway.contracts.dto.security.AccessServiceTokensDTO;

/**
 * Состояние клиента Шлюза, имеющего служебный токен для обновления токена доступа
 */
@SuppressWarnings("unused")
public class AccessServiceState extends AccessState {

    private String _serviceToken;
    private long _serviceTokenValidTo;

    /**
     * Получение служебного токена доступа
     *
     * @return Служебный токен доступа
     */
    public String getServiceToken() {
        return _serviceToken;
    }

    /**
     * Установка служебного токена доступа
     *
     * @param serviceToken Служебный токен доступа
     */
    public void setServiceToken(String serviceToken) {
        this._serviceToken = serviceToken;
    }

    /**
     * Получение срока действия служебного токена доступа в UTC в милисекундах от "эры UNIX" (01.01.1970 00:00:00.0)
     *
     * @return Срок действия служебного токена доступа в UTC в милисекундах
     */
    public long getServiceTokenValidTo() {
        return _serviceTokenValidTo;
    }

    /**
     * Установка срока действия служебного токена доступа в UTC в милисекундах от "эры UNIX" (01.01.1970 00:00:00.0)
     *
     * @param serviceTokenValidTo Срок действия служебного токена доступа в UTC в милисекундах
     */
    public void setServiceTokenValidTo(long serviceTokenValidTo) {
        this._serviceTokenValidTo = serviceTokenValidTo;
    }

    /**
     * Создание экземпляра класса {@link AccessServiceState} из токенов доступа и отметки времени
     *
     * @param tokens Токены доступа
     * @param now    Отметка времени, относительно которой рассчитывается срок действия токена
     * @return Экземпляр класса {@link AccessServiceState}
     */
    public static AccessServiceState from(AccessServiceTokensDTO tokens, long now) {

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
