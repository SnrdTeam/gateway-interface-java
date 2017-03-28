package com.adeptik.gateway.contracts.dto.agents;

/**
 * Данные агента
 */
@SuppressWarnings("unused")
public class GetAgentDTO {

    /**
     * Уникальный идентификатор агента
     */
    public long Id;

    /**
     * Имя агента
     */
    public String Name;

    /**
     * Признак доступности агента. Определяется по временной метке последнего доступа агента к шлюзу.
     */
    public boolean IsAvailable;
}
