package com.adeptik.gateway.contracts.dto;

import java.io.InputStream;

/**
 * Интерфейс бинарных данных
 */
public interface FormFile {

    /**
     * Открытие потока данных
     *
     * @return Поток данных
     */
    InputStream open();
}
