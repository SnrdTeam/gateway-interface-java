package com.adeptik.gateway.contracts.dto.security;

/**
 * Токены для доступа к функциональности шлюза
 */
public class AccessServiceTokensDTO {

    /**
     * Токен доступа
     */
    public TokenDTO Access;

    /**
     * Токен для сервисных операций (операций, которые не относятся к предметной области, выполняют служебные операции технического характера)
     * Например, данный токен может использоваться для обновления токена доступа
     */
    public TokenDTO Service;
}
