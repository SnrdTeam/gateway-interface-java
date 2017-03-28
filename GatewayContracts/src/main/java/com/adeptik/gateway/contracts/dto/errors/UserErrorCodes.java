package com.adeptik.gateway.contracts.dto.errors;

/**
 * Коды ошибок, связанные с пользователями шлюза
 */
@SuppressWarnings("unused")
public final class UserErrorCodes {

    /**
     * Некорректные имя пользователя или пароль
     */
    public static final int InvalidUserNameOrPassword = 1;

    /**
     * Указанная соль уже была использована.
     * Повторное использование соли запрещено в целях безопасности
     */
    public static final int SaltAlreadyUsed = 2;

    /**
     * Пользователь заблокирован
     */
    public static final int UserLocked = 3;
}
