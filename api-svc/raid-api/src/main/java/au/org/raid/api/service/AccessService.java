package au.org.raid.api.service;

import au.org.raid.api.repository.AccessTypeRepository;
import au.org.raid.api.repository.AccessTypeSchemaRepository;
import au.org.raid.idl.raidv2.model.Access;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessService {
    private final AccessTypeSchemaRepository accessTypeSchemaRepository;
    private final AccessTypeRepository accessTypeRepository;

    public Integer findAccessTypeId(final Access access) {
        final var accessTypeSchemaUri = access.getType().getSchemaUri();
        final var accessTypeUri = access.getType().getId();

        final var accessTypeSchemaRecord = accessTypeSchemaRepository.findByUri(accessTypeSchemaUri)
                .orElseThrow(() -> new RuntimeException("Access type schema not found %s".formatted(accessTypeSchemaUri)));

        final var accessTypeRecord = accessTypeRepository.findByUriAndSchemaId(accessTypeUri, accessTypeSchemaRecord.getId())
                .orElseThrow(() -> new RuntimeException("Access type %s not found in schema %s".formatted(
                        accessTypeUri, accessTypeSchemaUri
                )));

        return accessTypeRecord.getId();
    }
}
