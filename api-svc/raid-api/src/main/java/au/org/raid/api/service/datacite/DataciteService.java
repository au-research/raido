package au.org.raid.api.service.datacite;

import au.org.raid.api.factory.datacite.DataciteRequestFactory;
import au.org.raid.api.model.datacite.DataciteRequest;
import au.org.raid.api.spring.config.DataciteProperties;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataciteService {
    private final DataciteProperties properties;
    private final RestTemplate restTemplate;
    private final DataciteRequestFactory dataciteRequestFactory;

    private HttpHeaders generateDataciteRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = properties.getUser() + ":" + properties.getPassword();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.add("Authorization", "Basic " + encodedAuth);
        return headers;
    }

    @SneakyThrows
    public String createDataciteRaid(RaidCreateRequest createRequest, String handle){

        final String dataciteEndpoint = properties.getEndpoint();
        final String endpoint = dataciteEndpoint != null ? dataciteEndpoint + "/dois" : "https://api.test.datacite.org/dois";

        log.info("Datacite endpoint: {}", endpoint);

        if(properties.getUser() == null) {
            log.error("Datacite user is not set");
            return "not-set";
        }

        final HttpHeaders headers = generateDataciteRequestHeaders();

        final DataciteRequest dataciteRequest = dataciteRequestFactory.create(createRequest, handle);
        final HttpEntity<DataciteRequest> entity = new HttpEntity<>(dataciteRequest, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, JsonNode.class);
        return processResponse(response);
    }

    public String updateDataciteRaid(RaidUpdateRequest updateRequest, String handle) throws JsonProcessingException {

        if(properties.getUser() == null) {
            log.error("Datacite user is not set");
            return "not-set";
        }

        final String endpoint = properties.getEndpoint() + "/dois/" + handle;
        final HttpHeaders headers = generateDataciteRequestHeaders();

        final DataciteRequest dataciteRequest = dataciteRequestFactory.create(updateRequest, handle);
        final HttpEntity<DataciteRequest> entity = new HttpEntity<>(dataciteRequest, headers);
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.PUT, entity, JsonNode.class);
            return processResponse(response);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while creating Datacite RAID: {}", e.getMessage());
            throw new RuntimeException("Error creating handle", e);
        }
    }

    private String processResponse(ResponseEntity<JsonNode> response) {
        try {
            JsonNode responseBody = response.getBody();
            if (responseBody == null) {
                throw new IllegalStateException("Response body is null");
            }

            JsonNode dataNode = responseBody.path("data");
            if (dataNode.isMissingNode()) {
                throw new IllegalStateException("Response body does not contain 'data' node");
            }

            JsonNode idNode = dataNode.path("id");
            if (idNode.isMissingNode()) {
                throw new IllegalStateException("Data node does not contain 'id' node");
            }

            return idNode.asText();
        } catch (IllegalStateException e) {
            log.error("Error occurred while processing Datacite response: {}", e.getMessage());
            throw new RuntimeException("Error creating handle", e);
        }

    }
}