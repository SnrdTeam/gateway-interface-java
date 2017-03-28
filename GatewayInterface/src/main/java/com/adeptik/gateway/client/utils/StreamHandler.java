package com.adeptik.gateway.client.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Обработчик чтения потока
 */
public interface StreamHandler {

    /**
     * Вызывается при обработке потока
     *
     * @param inputStream Поток
     * @throws IOException Ошибка ввода-вывода
     */
    @SuppressWarnings("RedundantThrows")
    void handle(InputStream inputStream)
            throws IOException;
}
