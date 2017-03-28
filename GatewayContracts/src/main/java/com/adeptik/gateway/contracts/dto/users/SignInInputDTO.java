package com.adeptik.gateway.contracts.dto.users;

/**
 * Данные для авторизации пользователя шлюза
 */
@SuppressWarnings("WeakerAccess")
public class SignInInputDTO {

    /**
     * Имя пользователя
     */
    public String UserName;

    /**
     * Хэш пароля вместе с солью.
     * Берется хэш SHA-256 пароля в кодировке UTF-8, после чего в конец добавляются байты соли. От полученного массива байт вычислен хэш SHA-256.
     */
    public byte[] HashedPassword;

    /**
     * Соль, добавленная к хэшу SHA-256 пароля
     */
    public byte[] Salt;
}
