package com.adeptik.gateway.contracts.dto.agents;

import com.adeptik.gateway.contracts.model.ProblemAssignmentExecutionState;

/**
 * Обновление назначения задачи агентом
 */
public class PatchAssignmentDTO {

    /**
     * Состояние выполнения алгоритма
     */
    public ProblemAssignmentExecutionState State;
}
