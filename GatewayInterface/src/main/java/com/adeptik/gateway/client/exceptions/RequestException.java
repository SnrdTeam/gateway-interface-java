package com.adeptik.gateway.client.exceptions;

/**
 * Ошибка выполнения HTTP-запроса
 */
@SuppressWarnings("unused")
public class RequestException extends Exception {

    private final int _code;
    private final String _body;

    /**
     * Создание экземпляра класса {@link RequestException}
     *
     * @param code Код ответа
     * @param body Тело ответа
     */
    public RequestException(int code, String body) {
        super("Request error: code " + code);
        _code = code;
        _body = body;
    }

    /**
     * Возвращает код ответа
     *
     * @return Код ответа
     */
    public int getCode() {
        return _code;
    }

    /**
     * Возвращает тело ответа
     *
     * @return Тело ответа
     */
    public String getBody() {
        return _body;
    }
}
