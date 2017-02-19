package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.client.model.AccessState;
import com.adeptik.gateway.client.utils.MediaTypes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Базовый класс клиента шлюза
 */
public abstract class GatewayClient<TState> {

    private final URL _gatewayUrl;
    protected final TState _state;

    protected GatewayClient(URL gatewayUrl, TState state, Class<TState> stateClass)
            throws IllegalAccessException, InstantiationException {

        if (gatewayUrl == null)
            throw new IllegalArgumentException("gatewayUrl could not be null");
        if (stateClass == null)
            throw new IllegalArgumentException("stateClass could not be null");

        _gatewayUrl = gatewayUrl;
        _state = state != null ? state : stateClass.newInstance();
    }

    /**
     * Возвращает состояние клиента
     */
    public TState getState() {
        return _state;
    }


    public static AgentEnrollment asAgentEnrollment(URL gatewayUrl, AccessState state)
            throws InstantiationException, IllegalAccessException {

        return new AgentEnrollment(gatewayUrl, state);
    }

    public static Agent asAgent(URL gatewayUrl, AccessServiceState state)
            throws InstantiationException, IllegalAccessException {

        return new Agent(gatewayUrl, state);
    }

    public static User asUser(URL gatewayUrl, AccessServiceState state)
            throws IllegalAccessException, InstantiationException {

        return new User(gatewayUrl, state);
    }

    public static User asUser(URL gatewayUrl, String userName, String password)
            throws NoSuchAlgorithmException, IOException, InstantiationException, RequestException, IllegalAccessException {

        return new User(gatewayUrl, userName, password);
    }


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

    protected <T> RequestBody createJsonRequestBody(T bodyObject) {

        return RequestBody.create(MediaTypes.JSON, createGson().toJson(bodyObject));
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

    protected Gson createGson() {
        return new Gson();
    }

    protected long now() {

        return Calendar.getInstance().getTimeInMillis();
    }
}
