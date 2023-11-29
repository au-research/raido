package au.org.raid.api.service.raid;

import au.org.raid.api.exception.InvalidJsonException;
import au.org.raid.api.exception.InvalidVersionException;
import au.org.raid.api.exception.ResourceNotFoundException;
import au.org.raid.api.exception.UnknownServicePointException;
import au.org.raid.api.factory.IdFactory;
import au.org.raid.api.factory.RaidDtoFactory;
import au.org.raid.api.factory.RaidRecordFactory;
import au.org.raid.api.repository.RaidRepository;
import au.org.raid.api.repository.ServicePointRepository;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.apids.ApidsService;
import au.org.raid.api.service.apids.model.ApidsMintResponse;
import au.org.raid.api.service.datacite.DataciteService;
import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.raid.id.IdentifierParser.ParseProblems;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static au.org.raid.api.util.ExceptionUtil.runtimeException;
import static au.org.raid.api.util.Log.to;

/* Be careful with usage of @Transactional, see db-transaction-guideline.md */
@Component
@RequiredArgsConstructor
public class RaidStableV1Service {
    private static final Log log = to(RaidStableV1Service.class);
    private final ApidsService apidsSvc;
    private final DataciteService dataciteSvc;
    private final MetadataService metaSvc;
    private final TransactionTemplate tx;
    private final RaidRepository raidRepository;
    private final ServicePointRepository servicePointRepository;
    private final RaidRecordFactory raidRecordFactory;
    private final IdentifierParser idParser;
    private final ObjectMapper objectMapper;
    private final IdFactory idFactory;
    private final RaidChecksumService checksumService;
    private final RaidDtoFactory raidDtoFactory;
    private final RaidHistoryService raidHistoryService;


    public List<RaidDto> list(final Long servicePointId) {
        return raidRepository.findAllByServicePointId(servicePointId).stream()
                .map(raidDtoFactory::create)
                .toList();
    }

    public List<RaidDto> list(final ApiToken apiToken) {
        final var servicePointId = apiToken.getServicePointId();

        return raidRepository.findAllByServicePointOrNotConfidentialId(servicePointId).stream()
                .map(raidDtoFactory::create)
                .toList();
    }


    private IdentifierHandle parseHandleFromApids(
            ApidsMintResponse apidsResponse
    ) {
        var parseResult = idParser.parseHandle(apidsResponse.identifier.handle);

        if (parseResult instanceof ParseProblems problems) {
            log.with("handle", apidsResponse.identifier.handle).
                    with("problems", problems.getProblems()).
                    error("APIDS service returned malformed handle");
            throw runtimeException("APIDS service returned malformed handle: %s",
                    apidsResponse.identifier.handle);
        }

        return (IdentifierHandle) parseResult;
    }

    public IdentifierUrl mintRaidSchemaV1(
            final RaidCreateRequest request,
            final long servicePoint
    ) {
        /* this is the part where we want to make sure no TX is held open.
         * Maybe *this* should be marked tx.prop=never? */
        final var servicePointRecord =
                servicePointRepository.findById(servicePoint).orElseThrow(() ->
                        new UnknownServicePointException(servicePoint));

        final var apidsResponse = apidsSvc.mintApidsHandleContentPrefix(
                metaSvc::formatRaidoLandingPageUrl);

        IdentifierHandle handle = parseHandleFromApids(apidsResponse);
        var id = new IdentifierUrl(metaSvc.getMetaProps().getHandleUrlPrefix(), handle);
        request.setIdentifier(idFactory.create(id, servicePointRecord));

        final var raidDto = raidHistoryService.save(request);
        final var raidRecord = raidRecordFactory.create(raidDto);

        // TODO: Replace apids with datacite handle creation
        String dataciteHandle = dataciteSvc.getDataciteHandle();
        apidsResponse.identifier.handle = dataciteHandle;
        dataciteSvc.createDataciteRaid(request, dataciteHandle);

        tx.executeWithoutResult(status -> raidRepository.insert(raidRecord));

        return id;
    }

    @SneakyThrows
    public RaidDto update(
            final RaidUpdateRequest raid
    ) {
        final Integer version = raid.getIdentifier().getVersion();
        if (version == null) {
            throw new InvalidVersionException(version);
        }

        final IdentifierUrl identifierUrl;
        try {
            identifierUrl = idParser.parseUrlWithException(raid.getIdentifier().getId());
        } catch (ValidationFailureException e) {
            // it was already validated, so this shouldn't happen
            throw new RuntimeException(e);
        }
        String handle = identifierUrl.handle().format();

        final var existing = raidRepository.findByHandleAndVersion(handle, version)
                .orElseThrow(() -> new ResourceNotFoundException(handle));


        final var existingChecksum = checksumService.create(existing);
        final var updateChecksum = checksumService.create(raid);

        if (updateChecksum.equals(existingChecksum)) {
            return objectMapper.readValue(
                    existing.getMetadata().data(), RaidDto.class);
        }

        final var raidDto = raidHistoryService.save(raid);

        final var raidRecord = raidRecordFactory.create(raidDto);
        raidRepository.updateByHandleAndVersion(raidRecord, version);

        final var result = raidRepository.findByHandle(handle)
                .orElseThrow(() -> new ResourceNotFoundException(handle));

        try {
            dataciteSvc.updateDataciteRaid(raid, handle);
            return objectMapper.readValue(
                    result.getMetadata().data(), RaidDto.class);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException();
        }
    }

    public RaidDto read(String handle) {
        final var raidRecord = raidRepository.findByHandle(handle).
                orElseThrow(() -> new ResourceNotFoundException(handle));

        return raidDtoFactory.create(raidRecord);
    }

    public boolean isEditable(final ApiToken user, final long servicePointId) {
        final var servicePoint = servicePointRepository.findById(servicePointId)
                .orElseThrow(() -> new UnknownServicePointException(servicePointId));

        return user.getClientId().equals("RAIDO_API") || servicePoint.getAppWritesEnabled();
    }
}