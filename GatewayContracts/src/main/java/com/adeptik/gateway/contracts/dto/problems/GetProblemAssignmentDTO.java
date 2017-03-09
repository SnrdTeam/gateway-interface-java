package com.adeptik.gateway.contracts.dto.problems;

import com.adeptik.gateway.contracts.model.ProblemAssignmentState;

/**
 * Назначение задачи агенту
 */
public class GetProblemAssignmentDTO {

    /**
     * Идентификатор агента
     */
    public long AgentId;

    /**
     * Имя агента
     */
    public String AgentName;

    /**
     * Состояние назначения
     */
    public ProblemAssignmentState State;
}
