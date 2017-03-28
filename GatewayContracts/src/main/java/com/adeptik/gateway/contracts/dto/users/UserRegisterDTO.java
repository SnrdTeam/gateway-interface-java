package com.adeptik.gateway.contracts.dto.users;

/**
 * Данные для регистрации нового пользователя
 */
@SuppressWarnings("unused")
public class UserRegisterDTO {

    /**
     * Имя пользователя.
     * Может содержать только цифры, латинские буквы и знак '_'; начинается с латинской буквы.
     */
    public String Name;

    /**
     * Пароль пользователя.
     * Минимальная длина - 5 символов
     */
    public String Password;
}
