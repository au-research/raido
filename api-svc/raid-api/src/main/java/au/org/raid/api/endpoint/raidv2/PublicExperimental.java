package au.org.raid.api.endpoint.raidv2;

import au.org.raid.idl.raidv2.api.PublicExperimentalApi;
import au.org.raid.idl.raidv2.model.PublicServicePoint;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static au.org.raid.db.jooq.tables.ServicePoint.SERVICE_POINT;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
@CrossOrigin
@RequiredArgsConstructor
public class PublicExperimental implements PublicExperimentalApi {
    private final DSLContext db;

    /**
     * Transactional=SUPPORTS because when testing this out in AWS and I had
     * bad DB config, found out this method was creating a TX.  Doesn't need to do
     * that, so I added supports so that it would not create a TX if called at
     * top level.
     */
    @Override
    public ResponseEntity<List<PublicServicePoint>> publicListServicePoint() {
        return ResponseEntity.ok(db.
                select(
                        SERVICE_POINT.ID,
                        SERVICE_POINT.NAME).
                from(SERVICE_POINT).
                where(SERVICE_POINT.ENABLED.isTrue()).
                fetchInto(PublicServicePoint.class));
    }

}