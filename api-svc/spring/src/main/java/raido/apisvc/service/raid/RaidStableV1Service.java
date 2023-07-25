package raido.apisvc.service.raid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import raido.apisvc.exception.InvalidJsonException;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.exception.UnknownServicePointException;
import raido.apisvc.factory.IdFactory;
import raido.apisvc.factory.RaidRecordFactory;
import raido.apisvc.repository.RaidRepository;
import raido.apisvc.repository.ServicePointRepository;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.service.raid.id.IdentifierHandle;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.service.raid.id.IdentifierParser.ParseProblems;
import raido.apisvc.service.raid.id.IdentifierUrl;
import raido.apisvc.service.raid.validation.RaidoSchemaV1ValidationService;
import raido.apisvc.spring.security.raidv2.ApiToken;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.CreateRaidV1Request;
import raido.idl.raidv2.model.RaidDto;
import raido.idl.raidv2.model.UpdateRaidV1Request;

import java.util.List;

import static raido.apisvc.util.ExceptionUtil.runtimeException;
import static raido.apisvc.util.Log.to;

/* Be careful with usage of @Transactional, see db-transaction-guideline.md */
@Component
public class RaidStableV1Service {
  private static final Log log = to(RaidStableV1Service.class);
  private final ApidsService apidsSvc;
  private final MetadataService metaSvc;
  private final TransactionTemplate tx;
  private final RaidRepository raidRepository;
  private final ServicePointRepository servicePointRepository;
  private final RaidRecordFactory raidRecordFactory;
  private final IdentifierParser idParser;
  private final ObjectMapper objectMapper;

  private final IdFactory idFactory;

  public RaidStableV1Service(
    final DSLContext db,
    final ApidsService apidsSvc,
    final MetadataService metaSvc,
    final RaidoSchemaV1ValidationService validSvc,
    final TransactionTemplate tx,
    final RaidRepository raidRepository,
    final ServicePointRepository servicePointRepository,
    final RaidRecordFactory raidRecordFactory,
    IdentifierParser idParser,
    final ObjectMapper objectMapper, final IdFactory idFactory) {
    this.apidsSvc = apidsSvc;
    this.metaSvc = metaSvc;
    this.tx = tx;
    this.raidRepository = raidRepository;
    this.servicePointRepository = servicePointRepository;
    this.raidRecordFactory = raidRecordFactory;
    this.idParser = idParser;
    this.objectMapper = objectMapper;
    this.idFactory = idFactory;
  }

  public List<RaidDto> list(final Long servicePointId) {
    return raidRepository.findAllByServicePointId(servicePointId).stream().
      map(raid -> {
        try {
          return objectMapper.readValue(raid.raid().getMetadata().data(), RaidDto.class);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }).toList();
  }

  private IdentifierHandle parseHandleFromApids(
    ApidsMintResponse apidsResponse
  ) {
    var parseResult = idParser.parseHandle(apidsResponse.identifier.handle);
    
    if( parseResult instanceof ParseProblems problems ){
      log.with("handle", apidsResponse.identifier.handle).
        with("problems", problems.getProblems()).
        error("APIDS service returned malformed handle");
      throw runtimeException("APIDS service returned malformed handle: %s",
        apidsResponse.identifier.handle);
    }
   
    return (IdentifierHandle) parseResult; 
  }

  public IdentifierUrl mintRaidSchemaV1(
    final CreateRaidV1Request request,
    final long servicePoint
  ) {
    /* this is the part where we want to make sure no TX is held open.
     * Maybe *this* should be marked tx.prop=never? */
    final var servicePointRecord =
      servicePointRepository.findById(servicePoint).orElseThrow(() -> 
        new UnknownServicePointException(servicePoint) );

    final var apidsResponse = apidsSvc.mintApidsHandleContentPrefix(
      metaSvc::formatRaidoLandingPageUrl);


    IdentifierHandle handle = parseHandleFromApids(apidsResponse);
    var id = new IdentifierUrl(metaSvc.getMetaProps().handleUrlPrefix, handle);
    request.setId(idFactory.create(id, servicePointRecord));

    final var raidRecord = raidRecordFactory.create(
      request, apidsResponse, servicePointRecord );

    tx.executeWithoutResult(status -> raidRepository.insert(raidRecord));

    return id;
  }

  public RaidDto update(
    final UpdateRaidV1Request raid
  ) {
    final IdentifierUrl id;
    try {
      id = idParser.parseUrlWithException(raid.getId().getIdentifier());
    }
    catch( ValidationFailureException e ){
      // it was already validated, so this shouldn't happen
      throw new RuntimeException(e);
    }
    String handle = id.handle().format();
    
    final var existing = raidRepository.findByHandle(handle)
      .orElseThrow(() -> new ResourceNotFoundException(handle));

    final var raidRecord = raidRecordFactory.merge(raid, existing);

    //TODO: Check for changes before executing update

    tx.execute(status -> raidRepository.updateByHandleAndVersion(raidRecord));

    try {
      return objectMapper.readValue(
        raidRecord.getMetadata().data(), RaidDto.class );
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException();
    }
  }

  public RaidDto read(String handle){
    final var raid = raidRepository.findByHandle(handle).
      orElseThrow(() -> new ResourceNotFoundException(handle));

    try {
      return objectMapper.readValue(raid.getMetadata().data(), RaidDto.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isEditable(final ApiToken user, final long servicePointId) {
    final var servicePoint = servicePointRepository.findById(servicePointId)
      .orElseThrow(() -> new UnknownServicePointException(servicePointId));

    return user.getClientId().equals("RAIDO_API") || servicePoint.getAppWritesEnabled();
  }
}