package com.adeptik.gateway.contracts.dto.users;

/**
 * Обновляемые данные пользователя
 */
public class PatchUserDTO {

    /**
     * Признак того, что пользователь заблокирован, т.е. запрещен доступ к шлюзу
     */
    public Boolean IsLocked;
}

