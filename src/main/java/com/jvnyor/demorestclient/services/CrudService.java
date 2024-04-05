package com.jvnyor.demorestclient.services;

import com.jvnyor.demorestclient.dtos.CatRequestDTO;
import com.jvnyor.demorestclient.dtos.CatResponseDTO;
import com.jvnyor.demorestclient.services.exceptions.CatNotFoundException;
import com.jvnyor.demorestclient.services.exceptions.CatUnknownErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.function.Predicate;

@Service
public class CrudService {

    private final Logger logger = LoggerFactory.getLogger(CrudService.class);

    @Value("${crud-crud.base.url}")
    private String baseUrl;

    private final RestClient restClient;

    public CrudService(RestClient.Builder restClient) {
        this.restClient = restClient
                .requestInterceptor((request, body, execution) -> {
                    var requestBody = new String(body);
                    if (requestBody.isEmpty()) {
                        logger.info("{}: Request URI: {}", request.getMethod().name(), request.getURI());
                    } else {
                        logger.info("{}: Request URI: {}, Request Body: {}", request.getMethod().name(), request.getURI(), requestBody);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    public CatResponseDTO createCat(CatRequestDTO catRequestDTO) {
        return restClient.post()
                .uri(baseUrl + "/cats")
                .body(catRequestDTO)
                .retrieve()
                .onStatus(getHttpStatusCodeIsErrorPredicate(), (request, response) -> logUnknownErrorAndThrowAppropriatedException(request))
                .body(CatResponseDTO.class);
    }

    private void logUnknownErrorAndThrowAppropriatedException(HttpRequest request) {
        logger.error("{}: An unknown error occurred while processing the cat request. Request URI: {}", request.getMethod().name(), request.getURI());
        throw new CatUnknownErrorException();
    }

    public CatResponseDTO getCat(String id) {
        return restClient.get()
                .uri(baseUrl + "/cats/{id}", id)
                .retrieve()
                .onStatus(getHttpStatusCodeNotFoundPredicate(), (request, response) -> logNotFoundErrorRequestAndThrowAppropriatedException(request))
                .onStatus(getHttpStatusCodeIsErrorPredicate(), (request, response) -> logUnknownErrorAndThrowAppropriatedException(request))
                .body(CatResponseDTO.class);
    }

    private void logNotFoundErrorRequestAndThrowAppropriatedException(HttpRequest request) {
        logger.error("{}: Cat not found. Request URI: {}", request.getMethod().name(), request.getURI());
        throw new CatNotFoundException();
    }

    public void updateCat(String id, CatRequestDTO catRequestDTO) {
        restClient.put()
                .uri(baseUrl + "/cats/{id}", id)
                .body(catRequestDTO)
                .retrieve()
                .onStatus(getHttpStatusCodeNotFoundPredicate(), (request, response) -> logNotFoundErrorRequestAndThrowAppropriatedException(request))
                .onStatus(getHttpStatusCodeIsErrorPredicate(), (request, response) -> logUnknownErrorAndThrowAppropriatedException(request))
                .toBodilessEntity();
    }

    public void deleteCat(String id) {
        restClient.delete()
                .uri(baseUrl + "/cats/{id}", id)
                .retrieve()
                .onStatus(getHttpStatusCodeNotFoundPredicate(), (request, response) -> logNotFoundErrorRequestAndThrowAppropriatedException(request))
                .onStatus(getHttpStatusCodeIsErrorPredicate(), (request, response) -> logUnknownErrorAndThrowAppropriatedException(request))
                .toBodilessEntity();
    }

    private static Predicate<HttpStatusCode> getHttpStatusCodeIsErrorPredicate() {
        return HttpStatusCode::isError;
    }

    private static Predicate<HttpStatusCode> getHttpStatusCodeNotFoundPredicate() {
        return status -> status.value() == 404;
    }

    public List<CatResponseDTO> listCats() {
        return restClient.get()
                .uri(baseUrl + "/cats")
                .retrieve()
                .onStatus(getHttpStatusCodeIsErrorPredicate(), (request, response) -> logUnknownErrorAndThrowAppropriatedException(request))
                .body(new ParameterizedTypeReference<>() {
                });
    }
}