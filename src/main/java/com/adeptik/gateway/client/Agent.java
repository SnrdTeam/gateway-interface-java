package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.client.utils.StreamHandler;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.agents.AgentJobDTO;
import com.adeptik.gateway.contracts.dto.agents.PostAgentStateInputDTO;
import com.adeptik.gateway.contracts.dto.agents.PutAlgorithmStateDTO;
import com.adeptik.gateway.contracts.dto.security.TokenDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

public class Agent extends AccessRefreshGatewayClient {

    Agent(URL gatewayUrl, AccessServiceState state)
            throws IllegalAccessException, InstantiationException {

        super(
                gatewayUrl,
                state,
                Routes.AgentBaseUrl + "/token",
                "x-agent",
                "x-agent-service");
    }

    public AgentJobDTO postState(PostAgentStateInputDTO postAgentStateInputDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl + "/state")
                .post(createJsonRequestBody(postAgentStateInputDTO))
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, AgentJobDTO.class);
        }
    }

    public TokenDTO acceptAssignment()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl + "/assignment/accept")
                .patch(createEmptyRequestBody())
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, TokenDTO.class);
        }
    }

    public void getAlgorithmDefinition(long algorithmId, String[] runtimes, StreamHandler resultHandler)
            throws IOException, RequestException {

        if (runtimes == null)
            throw new IllegalArgumentException("runtimes not specified");
        if (resultHandler == null)
            throw new NullPointerException("resultHandler is null");

        Request request = createAuthorizedRequestBuilder(
                Routes.AlgorithmBaseUrl + "/" + algorithmId + "/definition?runtimes=" + String.join(",", runtimes))
                .get()
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
            resultHandler.handle(response.body().byteStream());
        }
    }

    public void getProblemDefinition(long problemId, StreamHandler resultHandler)
            throws IOException, RequestException {

        if (resultHandler == null)
            throw new NullPointerException("resultHandler is null");

        Request request = createAuthorizedRequestBuilder(Routes.ProblemBaseUrl + "/" + problemId + "/definition")
                .get()
                .build();
        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
            resultHandler.handle(response.body().byteStream());
        }
    }

    public void putAlgorithm(long algorithmId, PutAlgorithmStateDTO putAlgorithmStateDTO)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AgentBaseUrl + "/algorithm/" + algorithmId)
                .put(createJsonRequestBody(putAlgorithmStateDTO))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }
}
