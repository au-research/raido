package au.org.raid.api.service.datacite;

import au.org.raid.api.factory.datacite.*;
import au.org.raid.api.model.datacite.DataciteData;
import au.org.raid.api.model.datacite.DataciteDto;
import au.org.raid.api.model.datacite.DataciteIdentifier;
import au.org.raid.api.model.datacite.DataciteMintResponse;
import au.org.raid.api.spring.config.DataciteProperties;
import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class DataciteService {

    private final DataciteProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger log = LoggerFactory.getLogger(DataciteService.class);

    private HttpHeaders generateDataciteRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = properties.getUser() + ":" + properties.getPassword();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.add("Authorization", "Basic " + encodedAuth);
        return headers;
    }

    private String getDatacitePrefix(){
        if(properties.getPrefix() == null) {
            return "10.82841";
        }
        return properties.getPrefix();
    }

    private String getDataciteSuffix(){
        // TODO: This is a temporary solution to create a random identifier for the datacite handle
        // This should be replaced with a proper solution
        return UUID.randomUUID().toString().split("-")[0];
    }


    public DataciteData createPayloadForRequest(RaidCreateRequest createRequest, String handle) throws JsonProcessingException {
        DataciteDtoFactory dataciteDtoFactory = new DataciteDtoFactory();
        DataciteDto payloadForRequest = dataciteDtoFactory.create(createRequest, handle);

        DataciteData dataciteData = new DataciteData();
        dataciteData.setData(payloadForRequest);
        return dataciteData;
    }

    public DataciteData createPayloadForRequest(RaidUpdateRequest updateRequest, String handle) throws JsonProcessingException {
        DataciteDtoFactory dataciteDtoFactory = new DataciteDtoFactory();
        DataciteDto payloadForRequest = dataciteDtoFactory.create(updateRequest, handle);

        DataciteData dataciteData = new DataciteData();
        dataciteData.setData(payloadForRequest);
        return dataciteData;
    }

    public String createDataciteRaid(RaidCreateRequest createRequest, String handle) throws JsonProcessingException {

        if(properties.getUser() == null) {
            log.error("Datacite user is not set");
            return "not-set";
        }

        final String endpoint = properties.getEndpoint() + "/dois";
        final HttpHeaders headers = generateDataciteRequestHeaders();

        final DataciteData dataciteData = createPayloadForRequest(createRequest, handle);
        final HttpEntity<DataciteData> entity = new HttpEntity<>(dataciteData, headers);
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, JsonNode.class);
            return processResponse(response);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while creating Datacite RAID: {}", e.getMessage());
            throw new RuntimeException("Error creating handle", e);
        }
    }

    public String updateDataciteRaid(RaidUpdateRequest updateRequest, String handle) throws JsonProcessingException {

        if(properties.getUser() == null) {
            log.error("Datacite user is not set");
            return "not-set";
        }

        final String endpoint = properties.getEndpoint() + "/dois/" + handle;
        final HttpHeaders headers = generateDataciteRequestHeaders();

        final DataciteData dataciteData = createPayloadForRequest(updateRequest, handle);
        final HttpEntity<DataciteData> entity = new HttpEntity<>(dataciteData, headers);
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

    public DataciteMintResponse mintDataciteHandleContentPrefix(){
        String prefix = getDatacitePrefix();
        String suffix = getDataciteSuffix();

        DataciteMintResponse.Identifier.Property property = new DataciteMintResponse.Identifier.Property();
        property.index = 1;
        property.type = "DESC";
        property.value = "RAID handle";

        DataciteMintResponse.Identifier id = new DataciteMintResponse.Identifier();
        id.property = property;
        id.handle = prefix + "/" + suffix;

        DataciteMintResponse.Message message = new DataciteMintResponse.Message();
        message.type = null;
        message.value = null;

        DataciteMintResponse response = new DataciteMintResponse();
        response.setType("success");
        response.setIdentifier(id);
        response.setMessage(null);
        response.setTimestamp(LocalDateTime.now());

        return response;

    }
}