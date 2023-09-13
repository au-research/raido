package au.org.raid.inttest.endpoint.raidv2.admin;

import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.UpdateAuthzRequest;
import au.org.raid.idl.raidv2.model.UpdateAuthzRequestStatus;
import au.org.raid.inttest.IntegrationTestCase;
import feign.FeignException.InternalServerError;
import org.junit.jupiter.api.Test;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.api.test.util.BddUtil.*;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.db.jooq.api_svc.enums.UserRole.SP_ADMIN;
import static au.org.raid.db.jooq.api_svc.enums.UserRole.SP_USER;
import static au.org.raid.idl.raidv2.model.AuthzRequestStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Initially implemented to expose the usage of jooq DSL.asterisk() in the query,
 * which resulted in a query that's not forward-compatible with new columns
 * (see jooq-guideline.md).
 * <p>
 * Note that we often return 500 errors for stuff that is really auth based
 * failures, API really ought to be returning differentiated 4xx errors.
 * Be aware that a method could be throwing a 500 error because of some NPE or
 * other data error related to your inputs rather than the specific security
 * related condition you're trying to test.  You need to make sure (by eyeballing
 * the error logs) that the 500 failure is being caused by the thing you think
 * should be causing it.  But OTOH, beware intentionally communicating error
 * reasons for security failures via the API - a security auditor will report it
 * as a "potential partial compromise vector via information leakage".
 */
public class AuthzRequestTest extends IntegrationTestCase {
    private static final Log log = to(AuthzRequestTest.class);

    /**
     * user requests authorization to a service-point, admin approves the request,
     * the app-user is created.
     */
    @Test
    public void happyDayScenario() {
        String subject = idFactory.
                generateUniqueId("intTestUnapprovedGoogleSub", true);
        var unapprovedToken = bootstrapTokenSvc.fakeUnapprovedGoogle(subject);
        var unapprovedClient = unapprovedClient(unapprovedToken);
        var admin = adminExperimentalClientAs(operatorToken);


        WHEN("the authz-request is created");
        var createResult = unapprovedClient.updateAuthzRequest(
                new UpdateAuthzRequest().
                        servicePointId(RAIDO_SP_ID).
                        comments("intTest")).getBody();

        THEN("it should be returned from the read endpoint");
        var authzRequest = admin.readRequestAuthz(createResult.getAuthzRequestId()).getBody();
        assertThat(authzRequest.getSubject()).isEqualTo(subject);
        assertThat(authzRequest.getStatus()).isEqualTo(REQUESTED);

        AND("it should be returned from the list endpoint");
        var results = admin.listAuthzRequest().getBody();
        assertThat(results).anySatisfy(i -> {
            assertThat(i.getSubject()).isEqualTo(subject);
            assertThat(i.getId()).isEqualTo(createResult.getAuthzRequestId());
            assertThat(i.getStatus()).isEqualTo(REQUESTED);
        });

        AND("the app-user should not yet exist");
        var users = admin.listAppUser(RAIDO_SP_ID).getBody();
        assertThat(users).noneSatisfy(i ->
                assertThat(i.getSubject()).isEqualTo(subject));


        WHEN("the authz-request is approved");
        admin.updateAuthzRequestStatus(new UpdateAuthzRequestStatus().
                authzRequestId(createResult.getAuthzRequestId()).
                status(APPROVED).
                role(SP_USER.getLiteral())
        );

        THEN("the authz-request status should be updated");
        authzRequest = admin.readRequestAuthz(createResult.getAuthzRequestId()).getBody();
        assertThat(authzRequest.getSubject()).isEqualTo(subject);
        assertThat(authzRequest.getStatus()).isEqualTo(APPROVED);

        AND("the app-user should now exist");
        users = admin.listAppUser(RAIDO_SP_ID).getBody();
        assertThat(users).anySatisfy(i -> {
            assertThat(i.getSubject()).isEqualTo(subject);
            assertThat(i.getRole()).isEqualTo(SP_USER.getLiteral());
            assertThat(i.getRole()).isEqualTo(SP_USER.getLiteral());
        });
    }

    @Test
    public void rejectedAuthzRequestShouldNotCreateAppUser() {
        String subject = idFactory.
                generateUniqueId("intTestUnapprovedGoogleSub", true);
        var unapprovedToken = bootstrapTokenSvc.fakeUnapprovedGoogle(subject);
        var unapprovedClient = unapprovedClient(unapprovedToken);
        var admin = adminExperimentalClientAs(operatorToken);


        WHEN("the authz-request is created");
        var createResult = unapprovedClient.updateAuthzRequest(
                new UpdateAuthzRequest().
                        servicePointId(RAIDO_SP_ID).
                        comments("intTest")).getBody();

        THEN("the app-user should not yet exist");
        var users = admin.listAppUser(RAIDO_SP_ID).getBody();
        assertThat(users).noneSatisfy(i -> {
            assertThat(i.getSubject()).isEqualTo(subject);
        });

        WHEN("the authz-request is rejected");
        admin.updateAuthzRequestStatus(new UpdateAuthzRequestStatus().
                authzRequestId(createResult.getAuthzRequestId()).
                status(REJECTED)
        );

        THEN("the authz-request status should be updated");
        var authzRequest = admin.readRequestAuthz(createResult.getAuthzRequestId()).getBody();
        assertThat(authzRequest.getSubject()).isEqualTo(subject);
        assertThat(authzRequest.getStatus()).isEqualTo(REJECTED);

        AND("it should be returned from the list endpoint with new status");
        var results = admin.listAuthzRequest().getBody();
        assertThat(results).anySatisfy(i -> {
            assertThat(i.getSubject()).isEqualTo(subject);
            assertThat(i.getId()).isEqualTo(createResult.getAuthzRequestId());
            assertThat(i.getStatus()).isEqualTo(REJECTED);
        });

        THEN("the app-user should not exist");
        users = admin.listAppUser(RAIDO_SP_ID).getBody();
        assertThat(users).noneSatisfy(i ->
                assertThat(i.getSubject()).isEqualTo(subject));
    }

    @Test
    public void updateStatusEndpointShouldNotBeCallableByUnapprovedUsers() {
        String subject = idFactory.
                generateUniqueId("intTestUnapprovedGoogleSub", true);
        var unapprovedToken = bootstrapTokenSvc.fakeUnapprovedGoogle(subject);
        var unapprovedClient = unapprovedClient(unapprovedToken);
        var admin = adminExperimentalClientAs(unapprovedToken);


        GIVEN("the authz-request exists");
        var createResult = unapprovedClient.updateAuthzRequest(
                new UpdateAuthzRequest().
                        servicePointId(RAIDO_SP_ID).
                        comments("intTest")).getBody();

        EXPECT("unapproved user should not be able to call update-status endpoint");
        assertThatThrownBy(() ->
                admin.updateAuthzRequestStatus(new UpdateAuthzRequestStatus().
                        authzRequestId(createResult.getAuthzRequestId()).
                        role(SP_USER.getLiteral()).
                        status(APPROVED))
        ).
                // I think we should make this be some kind of 4xx? not sure which
                        isInstanceOf(InternalServerError.class);
    }

    @Test
    public void updateStatusEndpointShouldNotBeCallableByNormalUsers() {
        String subjectUnapproved = idFactory.
                generateUniqueId("intTestUnapprovedGoogleUser", true);
        String spUserSubject = idFactory.
                generateUniqueId("intTestWApprovedApiToken", true);
        var unapprovedToken = bootstrapTokenSvc.
                fakeUnapprovedGoogle(subjectUnapproved);
        var approvedSpUser = bootstrapTokenSvc.
                bootstrapToken(RAIDO_SP_ID, spUserSubject, SP_USER);
        var unapprovedClient = unapprovedClient(unapprovedToken);
        var spUserAdminClient = adminExperimentalClientAs(approvedSpUser);


        GIVEN("the authz-requests exist");
        var createResult = unapprovedClient.updateAuthzRequest(
                new UpdateAuthzRequest().servicePointId(RAIDO_SP_ID).comments("")).getBody();

        EXPECT("SP_USER should not be able to call update-status endpoint");
        assertThatThrownBy(() ->
                spUserAdminClient.updateAuthzRequestStatus(new UpdateAuthzRequestStatus().
                        authzRequestId(createResult.getAuthzRequestId()).
                        role(SP_USER.getLiteral()).
                        status(APPROVED))
        ).
                // again, I reckon it should be a 4xx
                        isInstanceOf(InternalServerError.class);

    }

    @Test
    public void updateStatusShouldNotBeCallableByAdminFromOtherSvcPoint() {
        String subjectUnapproved = idFactory.
                generateUniqueId("intTestUnapprovedGoogleUser", true);
        String spUserSubject = idFactory.
                generateUniqueId("intTestWApprovedApiToken", true);
        var unapprovedToken = bootstrapTokenSvc.
                fakeUnapprovedGoogle(subjectUnapproved);
        var otherSpId = findOtherPublicServicePoint(RAIDO_SP_ID).getId();
        var approvedSpUser = bootstrapTokenSvc.
                bootstrapToken(otherSpId, spUserSubject, SP_ADMIN);
        var unapprovedClient = unapprovedClient(unapprovedToken);
        var spUserAdminClient = adminExperimentalClientAs(approvedSpUser);


        GIVEN("the authz-requests exist");
        var createResult = unapprovedClient.updateAuthzRequest(
                new UpdateAuthzRequest().servicePointId(RAIDO_SP_ID).comments("")).getBody();

        EXPECT("SP_ADMIN from different SP should not be able to approve");
        assertThatThrownBy(() ->
                spUserAdminClient.updateAuthzRequestStatus(new UpdateAuthzRequestStatus().
                        authzRequestId(createResult.getAuthzRequestId()).
                        role(SP_USER.getLiteral()).
                        status(APPROVED))
        ).
                // again, I reckon it should be a 4xx
                        isInstanceOf(InternalServerError.class);
    }

}