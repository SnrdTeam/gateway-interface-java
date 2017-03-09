package com.adeptik.gateway.contracts.dto.agents;

import com.adeptik.gateway.contracts.dto.agents.jobs.InstallAlgorithmJobDTO;
import com.adeptik.gateway.contracts.dto.agents.jobs.SolveProblemJobDTO;

import java.util.Collection;

/**
 * Задание агенту
 */
public class AgentJobDTO {

    /**
     * Назначена ли задача, решение которой необходимо запустить
     */
    public SolveProblemJobDTO ProblemAssigned;

    /**
     * Алгоритмы, которые необходимо установить на агент
     */
    public Collection<InstallAlgorithmJobDTO> InstallAlgorithms;
}