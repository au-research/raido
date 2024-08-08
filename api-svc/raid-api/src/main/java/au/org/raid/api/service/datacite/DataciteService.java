package au.org.raid.api.service.datacite;

import au.org.raid.api.config.properties.DataciteProperties;
import au.org.raid.api.factory.HttpEntityFactory;
import au.org.raid.api.factory.datacite.DataciteRequestFactory;
import au.org.raid.api.model.datacite.DataciteRequest;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
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
    private final HttpEntityFactory httpEntityFactory;

    public void mint(final RaidCreateRequest request, final String handle,
                     final String repositoryId, final String password ){

        final DataciteRequest dataciteRequest = dataciteRequestFactory.create(request, handle);
        final HttpEntity<DataciteRequest> entity = httpEntityFactory.create(dataciteRequest, repositoryId, password);
        log.debug("Making POST request to Datacite: {}", properties.getEndpoint());

        restTemplate.exchange(properties.getEndpoint(), HttpMethod.POST, entity, JsonNode.class);
    }

    public void update(RaidUpdateRequest request, String handle,
                       final String repositoryId, final String password) {

        final var endpoint = "%s/%s".formatted(properties.getEndpoint(), handle);

        final DataciteRequest dataciteRequest = dataciteRequestFactory.create(request, handle);
        final HttpEntity<DataciteRequest> entity = httpEntityFactory.create(dataciteRequest, repositoryId, password);

        restTemplate.exchange(endpoint, HttpMethod.PUT, entity, JsonNode.class);
    }
}