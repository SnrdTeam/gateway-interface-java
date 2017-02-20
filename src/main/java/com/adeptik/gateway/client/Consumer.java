package com.adeptik.gateway.client;

import com.adeptik.gateway.client.exceptions.RequestException;
import com.adeptik.gateway.client.model.AccessServiceState;
import com.adeptik.gateway.client.utils.StreamHandler;
import com.adeptik.gateway.contracts.Routes;
import com.adeptik.gateway.contracts.dto.algorithms.GetAlgorithmDTO;
import com.adeptik.gateway.contracts.dto.algorithms.PostAlgorithmInputDTO;
import com.adeptik.gateway.contracts.dto.algorithms.PostAlgorithmOutputDTO;
import com.adeptik.gateway.contracts.dto.problems.GetProblemDTO;
import com.adeptik.gateway.contracts.dto.problems.PostProblemInputDTO;
import com.adeptik.gateway.contracts.dto.problems.PostProblemOutputDTO;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Consumer extends AccessRefreshGatewayClient {

    protected Consumer(URL gatewayUrl, AccessServiceState state)
            throws InstantiationException, IllegalAccessException {

        super(gatewayUrl, state, Routes.ConsumerBaseUrl + "/token", "x-consumer", "x-consumer-service");
    }

    public PostAlgorithmOutputDTO postAlgorithm(PostAlgorithmInputDTO postAlgorithmInputDTO)
            throws IOException, IllegalAccessException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AlgorithmBaseUrl)
                .post(createFormRequestBody(postAlgorithmInputDTO))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, PostAlgorithmOutputDTO.class);
        }
    }

    public void deleteAlgorithm(long algorithmId)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AlgorithmBaseUrl + "/" + algorithmId)
                .delete()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
        }
    }

    public Iterable<GetAlgorithmDTO> getAlgorithms()
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.AlgorithmBaseUrl)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response);
        }
    }

    public PostProblemOutputDTO postProblem(PostProblemInputDTO postProblemInputDTO)
            throws IOException, IllegalAccessException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ProblemBaseUrl)
                .post(createFormRequestBody(postProblemInputDTO))
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, PostProblemOutputDTO.class);
        }
    }

    public GetProblemDTO getProblem(long problemId)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ProblemBaseUrl + "/" + problemId)
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            return readJsonResponse(response, GetProblemDTO.class);
        }
    }

    public void getProblemSolution(long problemId, StreamHandler resultHandler)
            throws IOException, RequestException {

        Request request = createAuthorizedRequestBuilder(Routes.ProblemBaseUrl + "/" + problemId + "/solution")
                .get()
                .build();

        try (Response response = createHttpClient().newCall(request).execute()) {

            throwOnUnsuccessfulResponse(response);
            try (InputStream bodyStream = response.body().byteStream()) {
                resultHandler.handle(bodyStream);
            }
        }
    }
}