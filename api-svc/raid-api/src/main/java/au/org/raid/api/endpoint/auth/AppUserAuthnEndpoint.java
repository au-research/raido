package au.org.raid.api.endpoint.auth;

import au.org.raid.api.repository.AppUserRepository;
import au.org.raid.api.service.auth.RaidV2AppUserApiTokenService;
import au.org.raid.api.service.auth.RaidV2AppUserOidcService;
import au.org.raid.api.spring.config.environment.RaidoAuthnProps;
import au.org.raid.api.spring.security.ApiSafeException;
import au.org.raid.api.spring.security.IdProviderException;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.api.spring.security.raidv2.UnapprovedUserApiToken;
import au.org.raid.api.util.*;
import au.org.raid.db.jooq.enums.IdProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static au.org.raid.db.jooq.enums.IdProvider.ORCID;


@Slf4j
@RequestMapping
@RestController
public class AppUserAuthnEndpoint {
    public static final String IDP_URL = "/idpresponse";
    public static final String EMAIL_CLAIM = "email";
    private ObjectMapper map;

    private RaidV2AppUserOidcService userOidcSvc;
    private RaidV2AppUserApiTokenService userAuthzSvc;
    private RaidoAuthnProps authnProps;
    private AppUserRepository appUserRepo;

    public AppUserAuthnEndpoint(
            ObjectMapper map,
            RaidV2AppUserOidcService userOidcSvc,
            RaidV2AppUserApiTokenService userAuthzSvc,
            RaidoAuthnProps authnProps,
            AppUserRepository appUserRepo
    ) {
        this.map = map;
        this.userOidcSvc = userOidcSvc;
        this.userAuthzSvc = userAuthzSvc;
        this.authnProps = authnProps;
        this.appUserRepo = appUserRepo;
    }

    private static Optional<String> formatOrcidName(DecodedJWT idProviderJwt) {
        String givenName = idProviderJwt.getClaim("given_name").asString();
        String familyName = idProviderJwt.getClaim("family_name").asString();

        String formattedName = StringUtil.blankToDefault(givenName, "") +
                " " + StringUtil.blankToDefault(familyName, "");
        formattedName = formattedName.trim();
        if (StringUtil.isBlank(formattedName)) {
            return Optional.empty();
        }

        return Optional.of(formattedName);
    }

    public static boolean isAllowedRedirectUri(
            List<String> allowedUris,
            @Nullable String redirectUri
    ) {
        if (redirectUri == null) {
            return false;
        }
        String normalisedUri = redirectUri.trim().toLowerCase();
        return allowedUris.stream().anyMatch(normalisedUri::startsWith);
    }

    @GetMapping(IDP_URL)
    public void authenticate(
            HttpServletRequest req, HttpServletResponse res
    ) throws IOException, ApiSafeException {

        String idpResponseCode = req.getParameter("code");
        if (StringUtil.isNullOrEmpty(idpResponseCode)) {
            throw IdProviderException.idpException("no code provided in query params");
        }

        String stateValue = req.getParameter("state");
        if (StringUtil.isNullOrEmpty(stateValue)) {
            throw IdProviderException.idpException("no state provided in query params");
        }

        String decodedState = RestUtil.base64Decode(stateValue);

        var state = map.readValue(decodedState, AuthState.class);

        if (StringUtil.isNullOrEmpty(state.clientId)) {
            throw IdProviderException.idpException("no clientId provided in state");
        }

        if (!isAllowedRedirectUri(
                authnProps.getAllowedClientRedirectUris(), state.redirectUri)
        ) {
            throw ExceptionUtil.authFailed();
        }

        DecodedJWT idProviderJwt = userOidcSvc.
                exchangeOAuthCodeForIdToken(state.clientId, idpResponseCode);

    /* "email" isn't really email any more, going to rename it to
    "identity" or something.
    Orcid may not have an email, user doesn't have to make it public.
    Google always provides email.
    AAF, not sure if it's always there - can only say it's been there for
    everyone from ARDC so far. */
        String email = idProviderJwt.getClaim(EMAIL_CLAIM).asString();
        String subject = idProviderJwt.getSubject();

    /* Really not sure this is a good idea.
    It means that each time orcid users change their permissions (potentially
    three times between email, name, neither), the user will be counted as
    a separate user, because we use a tuple of [email, subject, clientId] to
    link the "id_token" to an app_user record.  That means they'll have to be
    authorized by someone again, too.
    I think we're going to have to just use subject.
    This new approach would align with our usage of app_user table for api-keys
    too. */
        if (userOidcSvc.mapIdProvider(state.clientId) == ORCID) {
            // try name fields, but that's also allowed to be private
            email = formatOrcidName(idProviderJwt).
                    // otherwise, fallback to the subject
                    // note the email field forces to lowercase, so no good using branding
                            orElseGet(() -> "orcid " + subject);
        }

        Guard.hasValue(email);
        Guard.hasValue(subject);

        var userRecord = appUserRepo.
                getAppUserRecord(email, subject, state.clientId);
        if (userRecord.isEmpty()) {
            // valid: authenticated via an IdP but not authorized/approved as a user
            res.sendRedirect("%s#id_token=%s".formatted(
                    state.redirectUri,
                    userAuthzSvc.sign(UnapprovedUserApiToken.UnapprovedUserApiTokenBuilder.anUnapprovedUserApiToken()
                            .withSubject(idProviderJwt.getSubject())
                            .withClientId(state.clientId)
                            .withEmail(email)
                            .build()
                    )
            ));
            return;
        }

        var user = userRecord.get();
        if (!user.getEnabled()) {
            // SP would need to look in their user list to know user is disabled
            throw ExceptionUtil.authFailed();
        }

        if (user.getIdProvider() == IdProvider.RAIDO_API) {
            throw ExceptionUtil.authFailed();
        }

    /* At this point, we transition from using the OAuth2/OIDC standard flow
    and terminology, to the concept of `api-token`*/
        res.sendRedirect("%s#id_token=%s".formatted(
                state.redirectUri,
                userAuthzSvc.sign(ApiToken.ApiTokenBuilder.anApiToken().
                        withAppUserId(user.getId()).
                        withServicePointId(user.getServicePointId()).
                        withSubject(idProviderJwt.getSubject()).
                        withClientId(state.clientId).
                        withEmail(email).
                        withRole(user.getRole().getLiteral()).
                        build()
                )
        ));
    }

    record AuthState(String redirectUri, String clientId) {
    }

}

