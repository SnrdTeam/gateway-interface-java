package com.adeptik.gateway.contracts.model;

/**
 * Состояния выполнения алгоритма на агенте
 */
public enum ProblemAssignmentExecutionState {

    /**
     * Запущен поиск решения задачи
     */
    Running(201),

    /**
     * Алгоритм успешно завершил выполнение
     */
    Completed(202),

    /**
     * Алгоритм завершил выполнение с ошибкой
     */
    Failed(203),

    /**
     * Выполнение алгоритма успешно прервано агентом
     */
    Interrupted(210);

    public final int Value;

    ProblemAssignmentExecutionState(int value) {

        Value = value;
    }
}
