package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.exception.InvalidAccessException;
import au.org.raid.api.service.raid.RaidService;
import au.org.raid.api.service.raid.ValidationFailureException;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.raid.id.IdentifierParser.ParseProblems;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.service.raid.validation.RaidoSchemaV1ValidationService;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import au.org.raid.db.jooq.api_svc.enums.Metaschema;
import au.org.raid.idl.raidv2.api.BasicRaidExperimentalApi;
import au.org.raid.idl.raidv2.model.*;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static au.org.raid.api.endpoint.Constant.MAX_EXPERIMENTAL_RECORDS;
import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.endpoint.raidv2.AuthzUtil.getApiToken;
import static au.org.raid.api.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static au.org.raid.api.service.raid.MetadataService.mapDb2Api;
import static au.org.raid.api.service.raid.MetadataService.mapDb2ApiV2;
import static au.org.raid.api.service.raid.RaidoSchemaV1Util.mintFailed;
import static au.org.raid.api.service.raid.id.IdentifierParser.mapProblemsToValidationFailures;
import static au.org.raid.api.util.DateUtil.local2Offset;
import static au.org.raid.api.util.ExceptionUtil.iae;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.hasValue;
import static au.org.raid.db.jooq.api_svc.tables.Raid.RAID;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.LEGACYMETADATASCHEMAV1;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

/* Be careful with usage of @Transactional, see db-transaction-guideline.md */
@Scope(proxyMode = TARGET_CLASS)
@RestController
public class BasicRaidExperimental implements BasicRaidExperimentalApi {
    private static final Log log = to(BasicRaidExperimental.class);

    private DSLContext db;
    private RaidService raidSvc;
    private RaidoSchemaV1ValidationService validSvc;
    private IdentifierParser idParser;

    public BasicRaidExperimental(
            DSLContext db,
            RaidService raidSvc,
            RaidoSchemaV1ValidationService validSvc,
            IdentifierParser idParser
    ) {
        this.db = db;
        this.raidSvc = raidSvc;
        this.validSvc = validSvc;
        this.idParser = idParser;
    }

    private static Condition createV2SearchCondition(RaidListRequestV2 req) {
        Condition searchCondition = DSL.trueCondition();
        if (hasValue(req.getPrimaryTitle())) {
            String primaryTitle = req.getPrimaryTitle().trim();
      /* client side should prevent this, don't need to worry about
       user-friendly error. */
            if (primaryTitle.length() < 5) {
                var iae = iae("primaryTitle is too short to list raids");
                log.with("primaryTitle", primaryTitle).error(iae.getMessage());
                throw iae;
            }
            primaryTitle = "%" + primaryTitle + "%";
            searchCondition = DSL.condition(RAID.PRIMARY_TITLE.like(primaryTitle));
        }
        return searchCondition;
    }



  /* Performance notes:
    - client sends json
    - Spring parses it out into all these different objects
    - we validate it then immediately and turn the metadata (which is most of
      the payload) back into json
    - save it to the DB
    - then turn the whole thing back into json, but now with a handle!
    And we're not even doing any validation of ORCID/ROR, etc. yet.
    That is a *lot* of heap garbage. Large raids are gonna wreck our memory usage.
    Especially if every update to single field is done by re-sending the full
    raid.
    Also, sending the full 200K raid data, just to update the startDate - the
    bandwidth bills will be monstrous.  BUT only on the way in - if we
    don't return the full raid from mint/update - that won't cost us.  But we'll
    need more read calls - which will cost. I'm thinking stable API should not
    return the raid, just the handle (or maybe IdBlock).
    A lot of API clients may just be dumping
    raids into the system (i.e. RDM) they don't care to display anything.
    */

    @Override
    public ReadRaidResponseV2 readRaidV2(ReadRaidV2Request req) {
        Guard.hasValue("must pass a handle", req.getHandle());
        var user = getApiToken();
        var data = raidSvc.readRaidResponseV2(req.getHandle());
        guardOperatorOrAssociated(user, data.getServicePointId());
        return data;
    }

    @Override
    public MintResponse mintRaidoSchemaV1(
            MintRaidoSchemaV1Request req
    ) {
        var mint = req.getMintRequest();

    /* when I did a "curl" with just an "empty object ", i.e.
    `{}`, we got an NPE at mint.getServicePointId() */
        Guard.notNull("request param may not be null", req);
        Guard.notNull("mintRequest field may not be null", mint);

        var user = getApiToken();
        guardOperatorOrAssociated(user, mint.getServicePointId());

        if (!raidSvc.isEditable(user, req.getMintRequest().getServicePointId())) {
            throw new InvalidAccessException("This service point does not allow Raids to be edited in the app.");
        }

        var failures = validSvc.validateRaidoSchemaV1(req.getMetadata());

        if (!failures.isEmpty()) {
            return mintFailed(failures);
        }

        IdentifierUrl id;
        try {
            id = raidSvc.mintRaidoSchemaV1(
                    req.getMintRequest().getServicePointId(),
                    req.getMetadata());
        } catch (ValidationFailureException e) {
            return mintFailed(failures);
        }

        return new MintResponse().success(true).
                raid(readRaidV2(new ReadRaidV2Request().handle(id.handle().format())));
    }

    @Override
    public List<RaidListItemV2> listRaidV2(RaidListRequestV2 req) {
        var user = getApiToken();
        guardOperatorOrAssociated(user, req.getServicePointId());

        return db.select(RAID.HANDLE, RAID.PRIMARY_TITLE, RAID.START_DATE,
                        RAID.CONFIDENTIAL, RAID.METADATA_SCHEMA, RAID.DATE_CREATED).
                from(RAID)
                .where(
                        RAID.SERVICE_POINT_ID.eq(req.getServicePointId())
                                .and(RAID.METADATA_SCHEMA.in(Metaschema.raido_metadata_schema_v1, Metaschema.legacy_metadata_schema_v1))
                                .and(createV2SearchCondition(req))
                )
                .orderBy(RAID.DATE_CREATED.desc())
                .limit(MAX_EXPERIMENTAL_RECORDS)
                .fetch(r -> new RaidListItemV2()
                        .handle(r.get(RAID.HANDLE))
                        .primaryTitle(r.get(RAID.PRIMARY_TITLE))
                        .startDate(r.get(RAID.START_DATE))
                        .createDate(local2Offset(r.get(RAID.DATE_CREATED)))
                        .metadataSchema(mapDb2Api(r.get(RAID.METADATA_SCHEMA))));
    }

    @Override
    public List<RaidListItemV3> listRaidV3(RaidListRequestV2 request) {
        var user = getApiToken();
        guardOperatorOrAssociated(user, request.getServicePointId());

        return db.select(RAID.HANDLE, RAID.PRIMARY_TITLE, RAID.START_DATE,
                        RAID.CONFIDENTIAL, RAID.METADATA_SCHEMA, RAID.DATE_CREATED)
                .from(RAID)
                .where(
                        RAID.SERVICE_POINT_ID.eq(request.getServicePointId())
                                .and(RAID.METADATA_SCHEMA.in(Metaschema.raido_metadata_schema_v1, Metaschema.legacy_metadata_schema_v1))
                                .and(createV2SearchCondition(request))
                ).
                orderBy(RAID.DATE_CREATED.desc())
                .limit(MAX_EXPERIMENTAL_RECORDS)
                .fetch(r -> new RaidListItemV3()
                        .handle(r.get(RAID.HANDLE))
                        .primaryTitle(r.get(RAID.PRIMARY_TITLE))
                        .startDate(r.get(RAID.START_DATE))
                        .createDate(local2Offset(r.get(RAID.DATE_CREATED)))
                        .metadataSchema(mapDb2ApiV2(r.get(RAID.METADATA_SCHEMA))));
    }

    /* Non-transactional, so that TX does not span the point when we talk
    to the external systems to validate (ROR, ORCID, DOI, etc.)
    See db-transaction-guideline.md. */
    @Override
    public MintResponse updateRaidoSchemaV1(UpdateRaidoSchemaV1Request req) {
        var user = getApiToken();
        var newData = req.getMetadata();

        if (!raidSvc.isEditable(user, req.getMetadata().getId().getIdentifierServicePoint())) {
            throw new InvalidAccessException("This service point does not allow Raids to be edited in the app.");
        }

        if (newData == null) {
            return mintFailed(METADATA_NOT_SET);
        }

        var newIdBlock = newData.getId();
        if (newIdBlock == null) {
            return mintFailed(ID_BLOCK_NOT_SET);
        }

        var idParse = idParser.parseUrl(newIdBlock.getIdentifier());
        if (idParse instanceof ParseProblems idProblems) {
            return mintFailed(mapProblemsToValidationFailures(idProblems));
        }
        var id = (IdentifierUrl) idParse;
        var handle = id.handle().format();

        if (req.getMetadata().getMetadataSchema() == LEGACYMETADATASCHEMAV1) {
            return mintFailed(CANNOT_UPDATE_LEGACY_SCHEMA);
        }

        // improve: don't need the svcPoint, wasteful to read it here
        var oldRaid = raidSvc.readRaidV2Data(handle).raid();
        guardOperatorOrAssociated(user, oldRaid.getServicePointId());

        var failures = raidSvc.updateRaidoSchemaV1(req.getMetadata(), oldRaid);
        if (failures.isEmpty()) {
            return new MintResponse().success(true).raid(
                    readRaidV2(new ReadRaidV2Request().handle(handle)));
        } else {
            return mintFailed(failures);
        }
    }

    @Override
    public MintResponse updateRaidoSchemaV2(UpdateRaidoSchemaV2Request request) {
        var user = getApiToken();
        var newData = request.getMetadata();

        if (!raidSvc.isEditable(user, request.getMetadata().getId().getIdentifierServicePoint())) {
            throw new InvalidAccessException("This service point does not allow Raids to be edited in the app.");
        }

        if (newData == null) {
            return mintFailed(METADATA_NOT_SET);
        }

        var newIdBlock = newData.getId();
        if (newIdBlock == null) {
            return mintFailed(ID_BLOCK_NOT_SET);
        }

        var idParse = idParser.parseUrl(newIdBlock.getIdentifier());
        if (idParse instanceof ParseProblems idProblems) {
            return mintFailed(mapProblemsToValidationFailures(idProblems));
        }
        var id = (IdentifierUrl) idParse;
        var handle = id.handle().format();

        if (request.getMetadata().getMetadataSchema() == au.org.raid.idl.raidv2.model.RaidoMetaschemaV2.LEGACYMETADATASCHEMAV1) {
            return mintFailed(CANNOT_UPDATE_LEGACY_SCHEMA);
        }

        // improve: don't need the svcPoint, wasteful to read it here
        var oldRaid = raidSvc.readRaidV2Data(handle).raid();
        guardOperatorOrAssociated(user, oldRaid.getServicePointId());

        var failures = raidSvc.updateRaidoSchemaV2(request.getMetadata(), oldRaid);
        if (failures.isEmpty()) {
            return new MintResponse().success(true).raid(
                    readRaidV2(new ReadRaidV2Request().handle(handle)));
        } else {
            return mintFailed(failures);
        }
    }

    @Override
    public MintResponse upgradeLegacyToRaidoSchema(
            UpdateRaidoSchemaV1Request req
    ) {
        var user = getApiToken();
        var newData = req.getMetadata();
        if (newData == null) {
            return mintFailed(METADATA_NOT_SET);
        }

        var idBlock = newData.getId();
        if (idBlock == null) {
            return mintFailed(ID_BLOCK_NOT_SET);
        }

        IdentifierUrl id;
        try {
            id = idParser.parseUrlWithException(idBlock.getIdentifier());
        } catch (ValidationFailureException e) {
            throw new RuntimeException(e);
        }
        String handle = id.handle().format();

        var oldRaid = raidSvc.readRaidV2Data(handle).raid();
        guardOperatorOrAssociated(user, oldRaid.getServicePointId());
    
    /* re-using updateRaidoSchemaV1() allows the upgrade process to make any 
    change, not just add the necessary data to make it compatible with the 
    upgrade schema.  
    But as long as the new structure obeys the schema rules, who cares? */
        var failures = raidSvc.upgradeRaidoSchemaV1(req.getMetadata(), oldRaid);
        if (failures.isEmpty()) {
            return new MintResponse().success(true).raid(
                    readRaidV2(new ReadRaidV2Request().handle(handle)));
        } else {
            return mintFailed(failures);
        }
    }
}
