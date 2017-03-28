package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.client.model.AccessState;
import com.adeptik.gateway.client.utils.MediaTypes;
import com.adeptik.gateway.client.utils.StreamHandler;
import com.adeptik.gateway.contracts.dto.FormFile;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import okhttp3.*;
import okio.BufferedSink;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Базовый класс клиента шлюза
 *
 * @param <TState> Тип состояния клиента Шлюза
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class GatewayClient<TState> {

    @SuppressWarnings({"Convert2Lambda", "Guava"})
    private final Predicate<Field> PublicFieldPredicate = new Predicate<Field>() {

        @Override
        public boolean apply(Field field) {

            return Modifier.isPublic(field.getModifiers());
        }
    };

    private final URL _gatewayUrl;
    protected final TState _state;

    private StateHandler _stateHandler;

    /**
     * Создание экземпляра класса {@link GatewayClient}
     *
     * @param gatewayUrl Адрес Шлюза
     * @param state      Состояние клиента Шлюза
     * @param stateClass Класс типа состояния клиента Шлюза
     */
    protected GatewayClient(URL gatewayUrl, TState state, Class<TState> stateClass) {

        if (gatewayUrl == null)
            throw new IllegalArgumentException("gatewayUrl could not be null");
        if (stateClass == null)
            throw new IllegalArgumentException("stateClass could not be null");

        _gatewayUrl = gatewayUrl;
        try {

            _state = state != null ? state : stateClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("stateClass is invalid. Could not create new instance", e);
        }
    }

    /**
     * Возвращает состояние клиента
     *
     * @return Объект, содержащий состояние клиента
     */
    public TState getState() {
        return _state;
    }

    /**
     * Установка обработчика изменения состояния клиента Шлюза
     *
     * @param stateHandler Обработчик изменения состояния клиента Шлюза
     */
    public void setStateHandler(StateHandler stateHandler) {

        _stateHandler = stateHandler;
    }

    /**
     * Вызывается при изменении состояния клиента Шлюза
     *
     * @throws IOException Ошибка ввода-вывода
     */
    protected void onStateChanged()
            throws IOException {

        if (_stateHandler != null)
            _stateHandler.onStateChanged();
    }


    /**
     * Создание клиента шлюза от имени потенциального агента
     *
     * @param gatewayUrl Адрес шлюза
     * @param state      Состояние клиента
     * @return Клиент Шлюза от имени потенциального Агента
     */
    public static AgentEnrollment asAgentEnrollment(URL gatewayUrl, AccessState state) {

        return new AgentEnrollment(gatewayUrl, state);
    }

    /**
     * Создание клиента Шлюза от имени Агента
     *
     * @param gatewayUrl Адрес Шлюза
     * @param state      Состояние клиента
     * @return Клиент Шлюза от имени Агента
     */
    public static Agent asAgent(URL gatewayUrl, AccessServiceState state) {

        return new Agent(gatewayUrl, state);
    }

    /**
     * Создание клиента Шлюза от имени Пользователя
     *
     * @param gatewayUrl Адрес Шлюза
     * @param state      Состояние клиента
     * @return Клиент Шлюза от имени Пользователя
     */
    public static User asUser(URL gatewayUrl, AccessServiceState state) {

        return new User(gatewayUrl, state);
    }

    /**
     * Создание клиента Шлюза от имени Пользователя
     *
     * @param gatewayUrl Адрес Шлюза
     * @param userName   Имя Пользователя для авторизации в Шлюзе
     * @param password   Пароль Пользователя для авторизации в Шлюзе
     * @return Клиент Шлюза от имени Пользователя
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка выполнения HTTP-запроса
     */
    public static User asUser(URL gatewayUrl, String userName, String password)
            throws IOException, RequestException {

        return new User(gatewayUrl, userName, password);
    }

    /**
     * Создание клиента Шлюза от имени Потребителя
     *
     * @param gatewayUrl Адрес Шлюза
     * @param state      Состояние клиента
     * @return Клиент Шлюза от имени Потребителя
     */
    public static Consumer asConsumer(URL gatewayUrl, AccessServiceState state) {

        return new Consumer(gatewayUrl, state);
    }


    /**
     * Создание HTTP-клиента, с помощью которого можно выполнить HTTP-запрос
     *
     * @return HTTP-клиент
     */
    protected OkHttpClient createHttpClient() {

        return new OkHttpClient()
                .newBuilder()
                .build();
    }

    /**
     * Создание построителя HTTP-запроса
     *
     * @param requestUri Путь к методу HTTP
     * @return Построитель HTTP-запроса
     * @throws MalformedURLException Некорректный путь к методу HTTP
     */
    protected Request.Builder createRequestBuilder(String requestUri)
            throws MalformedURLException {

        return new Request.Builder()
                .url(new URL(_gatewayUrl, requestUri));
    }

    /**
     * Проверка успешности HTTP-запроса.
     * Если запрос неуспешен, генерируется исключение {@link RequestException}
     *
     * @param response Ответ запроса HTTP
     * @throws RequestException Неуспешный ответ на запрос HTTP
     */
    protected void throwOnUnsuccessfulResponse(Response response)
            throws RequestException {

        if (!response.isSuccessful())
            throw new RequestException(response.code(), response.message());
    }

    /**
     * Создание тела запроса в формате JSON
     *
     * @param bodyObject Объект тела запроса, который будет преобразован в JSON
     * @param valueType  Класс типа значения тела запроса
     * @param <T>        Тип значения тела запроса, на основе которого будет произвведена сериализация в JSON
     * @param <TValue>   Конечный тип значения тела запроса
     * @return Экземпляр класса {@link RequestBody}
     */
    protected <T, TValue extends T> RequestBody createJsonRequestBody(TValue bodyObject, Class<T> valueType) {

        return RequestBody.create(MediaTypes.JSON, createGson().toJson(bodyObject, valueType));
    }

    /**
     * Создание тела запроса в формате formData
     *
     * @param bodyObject Объект тела запроса, который будет преобразован в formData
     * @param <TValue>   Тип значения тела запроса
     * @return Экземпляр класса {@link RequestBody}
     * @throws IllegalAccessException Не удается получить доступ к члену класса
     */
    protected <TValue> RequestBody createFormRequestBody(TValue bodyObject)
            throws IllegalAccessException {

        if (bodyObject == null)
            throw new NullPointerException("bodyObject cannot be null");

        //noinspection Guava
        FluentIterable<Field> fields = FluentIterable.of(bodyObject
                .getClass()
                .getFields())
                .filter(PublicFieldPredicate);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Field field : fields) {

            if (FormFile.class.isAssignableFrom(field.getType())) {

                FormFile formFileFieldValue = (FormFile) field.get(bodyObject);
                builder.addFormDataPart(field.getName(), null, new RequestBody() {

                    @Override
                    public MediaType contentType() {
                        return MediaTypes.OctetStream;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {

                        try (InputStream algorithmDefinitionStream = formFileFieldValue.open()) {

                            ByteStreams.copy(algorithmDefinitionStream, sink.outputStream());
                        }
                    }
                });
            } else
                builder.addFormDataPart(field.getName(), field.get(bodyObject).toString());
        }

        return builder.build();
    }

    /**
     * Создание пустого тела запроса
     *
     * @return Экземпляр класса {@link RequestBody}
     */
    protected RequestBody createEmptyRequestBody() {

        return RequestBody.create(null, new byte[0]);
    }

    /**
     * Чтение тела ответа из JSON
     *
     * @param response Ответ HTTP
     * @param typeOfT  Тип значения тела ответа HTTP
     * @param <T>      Тип значения тела ответа HTTP
     * @return Значение тела ответа HTTP
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка выполнения HTTP-запроса
     */
    protected <T> T readJsonResponse(Response response, Type typeOfT)
            throws IOException, RequestException {

        throwOnUnsuccessfulResponse(response);

        Gson gson = createGson();
        try (JsonReader jsonReader = new JsonReader(response.body().charStream())) {
            return gson.fromJson(jsonReader, typeOfT);
        }
    }

    /**
     * Чтение тела ответа из JSON
     *
     * @param response Ответ HTTP
     * @param <TItem>  Тип элемента значения тела ответа HTTP
     * @return Значение тела ответа HTTP
     * @throws IOException      Ошибка ввода-вывода
     * @throws RequestException Ошибка выполнения HTTP-запроса
     */
    protected <TItem> Iterable<TItem> readJsonResponse(Response response)
            throws IOException, RequestException {

        return readJsonResponse(
                response,
                new TypeToken<ArrayList<TItem>>() {
                }.getType());
    }

    /**
     * Чтение тела ответа из файла
     *
     * @param response      Ответ HTTP
     * @param resultHandler Обработчик чтения тела ответа HTTP
     * @throws RequestException Ошибка выполнения HTTP-запроса
     * @throws IOException      Ошибка ввода-вывода
     */
    protected void readFileResponse(Response response, StreamHandler resultHandler)
            throws RequestException, IOException {

        if (resultHandler == null)
            throw new NullPointerException("resultHandler is null");

        throwOnUnsuccessfulResponse(response);
        try (InputStream bodyStream = response.body().byteStream()) {
            resultHandler.handle(bodyStream);
        }
    }

    /**
     * Создание экземпляра класса {@link Gson}
     *
     * @return Экземпляр класса {@link Gson}
     */
    protected Gson createGson() {

        return new GsonBuilder()
                .create();
    }

    /**
     * Получение текущего момента времени в UTC в количестве милисекунд с "эры UNIX" (01.01.1970 00:00:00.0)
     *
     * @return Количество милисекунд с "эры UNIX" (01.01.1970 00:00:00.0)
     */
    protected long now() {

        return Calendar.getInstance().getTimeInMillis();
    }


    /**
     * Обработчик изменения состояния клиента Шлюза
     */
    public interface StateHandler {

        /**
         * Вызывается при изменении состояния клиента Шлюза
         *
         * @throws IOException Ошибка ввода-вывода
         */
        void onStateChanged()
                throws IOException;
    }
}
