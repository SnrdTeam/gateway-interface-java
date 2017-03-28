package com.adeptik.gateway.contracts.dto.algorithms;


import com.adeptik.gateway.contracts.dto.FormFile;

/**
 * Данные создаваемого в шлюзе алгоритме
 */
public class PostAlgorithmInputDTO {

    /**
     * Имя алгоритма, используемое в системе
     */
    public String Name;

    /**
     * Определение алгоритма
     */
    public FormFile AlgorithmDefinition;
}

