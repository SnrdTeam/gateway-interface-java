package com.adeptik.gateway.contracts.dto.enrollment;

/**
 * Данные добавляемого запроса агента на присоединение к шлюзу
 */
@SuppressWarnings("unused")
public class PostAgentEnrollmentInputDTO {

    /**
     * Имя потенциального агента
     */
    public String AgentName;

    /**
     * Комментарий к запросу агента на присоединение к шлюзу
     */
    public String Comment;
}
