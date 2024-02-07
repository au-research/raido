package au.org.raid.api.service.datacite;

import au.org.raid.api.factory.HttpHeadersFactory;
import au.org.raid.api.factory.datacite.DataciteRequestFactory;
import au.org.raid.api.model.datacite.DataciteRequest;
import au.org.raid.api.spring.config.DataciteProperties;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataciteService {
    private final DataciteProperties properties;
    private final RestTemplate restTemplate;
    private final DataciteRequestFactory dataciteRequestFactory;
    private final HttpHeadersFactory httpHeadersFactory;

    public void mint(final RaidCreateRequest createRequest, final String handle,
                     final String repositoryId, final String password ){

        final HttpHeaders headers = httpHeadersFactory.createBasicAuthHeaders(repositoryId, password);

        final DataciteRequest dataciteRequest = dataciteRequestFactory.create(createRequest, handle);
        final HttpEntity<DataciteRequest> entity = new HttpEntity<>(dataciteRequest, headers);

        restTemplate.exchange(properties.getEndpoint(), HttpMethod.POST, entity, JsonNode.class);
    }

    public void update(RaidUpdateRequest updateRequest, String handle,
                       final String repositoryId, final String password) {

        final var endpoint = "%s/%s".formatted(properties.getEndpoint(), handle);

        final HttpHeaders headers = httpHeadersFactory.createBasicAuthHeaders(repositoryId, password);

        final DataciteRequest dataciteRequest = dataciteRequestFactory.create(updateRequest, handle);
        final HttpEntity<DataciteRequest> entity = new HttpEntity<>(dataciteRequest, headers);

        restTemplate.exchange(endpoint, HttpMethod.PUT, entity, JsonNode.class);
    }
}