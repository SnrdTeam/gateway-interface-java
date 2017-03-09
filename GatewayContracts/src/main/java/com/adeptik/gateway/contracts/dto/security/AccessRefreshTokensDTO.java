package com.adeptik.gateway.contracts.dto.security;

/**
 * Токены для доступа к функциональности шлюза
 */
public class AccessRefreshTokensDTO {

    /**
     * Токен доступа
     */
    public TokenDTO Access;

    /**
     * Токен для обновления токена
     */
    public TokenDTO Service;
}
