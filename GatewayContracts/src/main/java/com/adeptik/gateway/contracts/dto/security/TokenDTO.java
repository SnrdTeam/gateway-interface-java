package com.adeptik.gateway.contracts.dto.security;

/**
 * Информация о выданном токене
 */
public class TokenDTO {

    /**
     * Токен
     */
    public String Token;

    /**
     * Срок действия токена в милисекундах.
     * Если null, то токен действет бесконечно
     */
    public Long ExpiresIn;
}
