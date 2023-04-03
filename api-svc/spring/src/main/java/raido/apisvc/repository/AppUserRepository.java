package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import raido.apisvc.util.Guard;
import raido.db.jooq.api_svc.tables.records.AppUserRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;

@Repository
public class AppUserRepository {
  private DSLContext db;

  public AppUserRepository(DSLContext db) {
    this.db = db;
  }

  /** This should be cached read, otherwise we're gonna be doing 
   this for every single API call for a user.  Use Caffeine. */
  public Optional<AppUserRecord> getApiKeyRecord(
    long servicePointId,
    String identity,
    String clientId,
    String subject
  ){
    return db.fetchOptional(APP_USER, DSL.and(
      APP_USER.SERVICE_POINT_ID.eq(servicePointId),
      APP_USER.EMAIL.eq(identity),
      APP_USER.CLIENT_ID.eq(clientId),
      APP_USER.SUBJECT.eq(subject)));
  }

  /**
   This is for originating authentication, so don't need or want it to be 
   cached.
   */
  public Optional<AppUserRecord> getAppUserRecord(
    String email,
    String subject,
    String clientId
  ){
    Guard.hasValue(email);
    Guard.hasValue(clientId);
    Guard.hasValue(subject);

    // service_point_id_fields_active_idx enforces uniqueness 
    return db.select().
      from(APP_USER).
      where(
        /* had to lowercase() the email because the name fields (that can 
        end up in the "email" field) come back from orcid with initcaps 
        and it wasn't matching.
        The real fix is to start matching on only "subject" claim. */
        APP_USER.EMAIL.eq(email.toLowerCase()).
          and(APP_USER.CLIENT_ID.eq(clientId)).
          and(APP_USER.SUBJECT.eq(subject)).
          and(APP_USER.ENABLED.isTrue())
      ).fetchOptionalInto(AppUserRecord.class);
  }

  /** This should be cached read, otherwise we're gonna be doing 
   this for every single API call for a user.  Use Caffeine.
   */
  public Optional<AppUserRecord> getAppUserRecord(
    long appUserId
  ){
    return db.fetchOptional(APP_USER, APP_USER.ID.eq(appUserId));
  }


}
