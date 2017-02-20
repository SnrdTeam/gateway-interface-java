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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Базовый класс клиента шлюза
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
     * Создание клиента шлюза от имени потенциального агента
     *
     * @param gatewayUrl Адрес шлюза
     * @param state      Состояние клиента
     * @return Клиент шлюза от имени потенциального агента
     */
    public static AgentEnrollment asAgentEnrollment(URL gatewayUrl, AccessState state) {

        return new AgentEnrollment(gatewayUrl, state);
    }

    public static Agent asAgent(URL gatewayUrl, AccessServiceState state) {

        return new Agent(gatewayUrl, state);
    }

    public static User asUser(URL gatewayUrl, AccessServiceState state) {

        return new User(gatewayUrl, state);
    }

    public static User asUser(URL gatewayUrl, String userName, String password)
            throws NoSuchAlgorithmException, IOException, RequestException {

        return new User(gatewayUrl, userName, password);
    }

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

    protected Request.Builder createRequestBuilder(String requestUri)
            throws MalformedURLException {

        return new Request.Builder()
                .url(new URL(_gatewayUrl, requestUri));
    }

    protected void throwOnUnsuccessfulResponse(Response response)
            throws RequestException {

        if (!response.isSuccessful())
            throw new RequestException(response.code(), response.message());
    }

    protected <T, TValue extends T> RequestBody createJsonRequestBody(TValue bodyObject, Class<T> valueType) {

        return RequestBody.create(MediaTypes.JSON, createGson().toJson(bodyObject, valueType));
    }

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

    protected RequestBody createEmptyRequestBody() {

        return RequestBody.create(null, new byte[0]);
    }

    protected <T> T readJsonResponse(Response response, Type typeOfT) throws IOException, RequestException {

        throwOnUnsuccessfulResponse(response);

        Gson gson = createGson();
        try (JsonReader jsonReader = new JsonReader(response.body().charStream())) {
            return gson.fromJson(jsonReader, typeOfT);
        }
    }

    protected <TItem> Iterable<TItem> readJsonResponse(Response response)
            throws IOException, RequestException {

        return readJsonResponse(
                response,
                new TypeToken<ArrayList<TItem>>() {
                }.getType());
    }

    protected void readFileResponse(Response response, StreamHandler resultHandler)
            throws RequestException, IOException {

        if (resultHandler == null)
            throw new NullPointerException("resultHandler is null");

        throwOnUnsuccessfulResponse(response);
        try (InputStream bodyStream = response.body().byteStream()) {
            resultHandler.handle(bodyStream);
        }
    }

    protected Gson createGson() {

        return new GsonBuilder()
                .create();
    }

    protected long now() {

        return Calendar.getInstance().getTimeInMillis();
    }
}
