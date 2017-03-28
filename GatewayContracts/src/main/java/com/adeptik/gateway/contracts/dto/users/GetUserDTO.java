package com.adeptik.gateway.contracts.dto.users;

/**
 * Информация о пользователе системы
 */
public class GetUserDTO {

    /**
     * Имя (логин) пользователя
     */
    public String Name;

    /**
     * Признак того, что пользователь заблокирован
     */
    public boolean IsLocked;
}

