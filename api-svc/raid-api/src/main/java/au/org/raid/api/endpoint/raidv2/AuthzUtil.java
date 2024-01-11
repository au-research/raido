package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.endpoint.message.RaidApiMessage;
import au.org.raid.api.exception.CrossAccountAccessException;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.api.util.ExceptionUtil;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.ObjectUtil;
import au.org.raid.db.jooq.enums.IdProvider;

import static au.org.raid.db.jooq.enums.UserRole.OPERATOR;
import static au.org.raid.db.jooq.enums.UserRole.SP_ADMIN;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

public class AuthzUtil {
    /* Hardcoded, we know this statically because we hardcoded the sequence to
     20M and raido is the first SP inserted via flyway. */
    public static final long RAID_AU_SP_ID = 20_000_000;

    /**
     * This will fail if the authentication is not a AuthzTokenPayload
     */
    public static ApiToken getApiToken() {
        var authentication = getContext().getAuthentication();
        if (authentication instanceof ApiToken apiToken) {
            return apiToken;
        }

        throw ExceptionUtil.authFailed();
    }

    /**
     * User must be an admin associated specifically with the raido SP.
     * Originally implemented so I could write the migration endpoint that can
     * insert raids across service points (i.e. migrating RDM and NotreDame raids,
     * etc.) - without having to allow api-keys to have an "operator" role.
     */
    public static void guardRaidoAdminApiKey(ApiToken user) {
        if (!ObjectUtil.areEqual(user.getClientId(), IdProvider.RAIDO_API.getLiteral())) {
      /* IMPROVE: there really ought to be a better/more explicit way to tell 
      if a given user is an api-key or a real user. */
            throw ExceptionUtil.iae(RaidApiMessage.ONLY_RAIDO_ADMIN);
        }

        if (!ObjectUtil.areEqual(user.getRole(), SP_ADMIN.getLiteral())) {
            throw ExceptionUtil.iae(RaidApiMessage.ONLY_RAIDO_ADMIN);
        }

        if (!ObjectUtil.areEqual(user.getServicePointId(), RAID_AU_SP_ID)) {
            throw ExceptionUtil.iae(RaidApiMessage.ONLY_RAIDO_ADMIN);
        }
    }

    /**
     * User must be an OP, or must be "associated" to the SP passed.
     * Right now, "associated" means "directly associated", but
     * later might mean a more indirect association.
     */
    public static void guardOperatorOrAssociated(
            ApiToken user,
            Long servicePointId
    ) {
        Guard.notNull("user must be set", user);
        Guard.isPositive("servicePointId must be set", servicePointId);
        if (ObjectUtil.areEqual(user.getRole(), OPERATOR.getLiteral())) {
            // operator can update requests for any service point
            return;
        }

        if (!ObjectUtil.areEqual(servicePointId, user.getServicePointId())) {
            var exception = new CrossAccountAccessException(servicePointId);
            throw exception;
        }
    }

    public static void guardOperatorOrAssociatedSpAdmin(
            ApiToken user,
            Long servicePointId
    ) {
        if (ObjectUtil.areEqual(user.getRole(), OPERATOR.getLiteral())) {
            // operator can update any service point
            return;
        }

        if (ObjectUtil.areEqual(user.getRole(), SP_ADMIN.getLiteral())) {
            if (ObjectUtil.areEqual(servicePointId, user.getServicePointId())) {
                // admin can update their own service point
                return;
            }

            // SP_ADMIN is not allowed to do stuff to other SP's
            throw ExceptionUtil.iae(RaidApiMessage.DISALLOWED_X_SVC_CALL);
        }

        throw ExceptionUtil.iae(RaidApiMessage.ONLY_OP_OR_SP_ADMIN);
    }

    public static void guardOperatorOrAssociatedSpUser(
            ApiToken user,
            Long servicePointId
    ) {
        if (ObjectUtil.areEqual(user.getRole(), OPERATOR.getLiteral())) {
            // operator can update any service point
            return;
        }

        if (ObjectUtil.areEqual(servicePointId, user.getServicePointId())) {
            // sp_user and admin can list raids for their own SP
            return;
        }

        // not allowed to read raids from other SP's
        throw ExceptionUtil.iae(RaidApiMessage.DISALLOWED_X_SVC_CALL);
    }

    public static boolean isOperatorOrSpAdmin(
            ApiToken user
    ) {
        return ObjectUtil.areEqual(user.getRole(), OPERATOR.getLiteral()) ||
                ObjectUtil.areEqual(user.getRole(), SP_ADMIN.getLiteral());
    }

    public static void guardOperatorOrSpAdmin(
            ApiToken user
    ) {
        if (isOperatorOrSpAdmin(user)) {
            return;
        }

        var iae = ExceptionUtil.iae(RaidApiMessage.ONLY_OP_OR_SP_ADMIN);
        throw iae;
    }

}
