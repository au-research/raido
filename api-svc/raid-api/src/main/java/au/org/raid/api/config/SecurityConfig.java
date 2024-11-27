package au.org.raid.api.config;

import au.org.raid.api.exception.ResourceNotFoundException;
import au.org.raid.api.exception.ServicePointNotFoundException;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.ServicePointService;
import au.org.raid.api.util.SchemaValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAnyRole;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    public static final String SERVICE_POINT_GROUP_ID_CLAIM = "service_point_group_id";
    private static final String RAID_USER_ROLE = "raid-user";
    private static final String RAID_ADMIN_ROLE = "raid-admin";
    private static final String RAID_SEARCHER_ROLE = "raid-searcher";
    private static final String SERVICE_POINT_USER_ROLE = "service-point-user";
    private static final String OPERATOR_ROLE = "operator";
    private static final String CONTRIBUTOR_WRITER_ROLE = "contributor-writer";
    private static final String GROUPS = "groups";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String RAID_API = "/raid";
    private static final String SERVICE_POINT_API = "/service-point";
    private static final String ADMIN_RAIDS_CLAIM = "admin_raids";
    private static final String USER_RAIDS_CLAIM = "user_raids";

    private final KeycloakLogoutHandler keycloakLogoutHandler;
    private final ServicePointService servicePointService;
    private final RaidHistoryService raidHistoryService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui*/**").permitAll()
                        .requestMatchers("/docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(new AntPathRequestMatcher(RAID_API + "/", "GET"))
                        //TODO: Any service point user but embargoed raids should only be visible to service point
                        // owners or raid users/admins with permissions for the raid
                        .access(AuthorizationManagers.anyOf(
                                hasAnyRole(SERVICE_POINT_USER_ROLE, RAID_ADMIN_ROLE)
                        ))
                        //TODO: Available to any user on same service point unless embargoed then on service-point-owner
                        .requestMatchers(new AntPathRequestMatcher(RAID_API + "/**", "GET"))
                        .access(AuthorizationManagers.anyOf(
                                anyServicePointUserUnlessEmbargoed(),
                                servicePointOwner(),
                                hasRaidPermissions(RAID_ADMIN_ROLE, ADMIN_RAIDS_CLAIM),
                                hasRaidPermissions(RAID_USER_ROLE, USER_RAIDS_CLAIM)
                        ))
                         .requestMatchers(new AntPathRequestMatcher(RAID_API + "/**", "POST"))
                        .hasAnyRole(SERVICE_POINT_USER_ROLE, RAID_ADMIN_ROLE)
                        .requestMatchers(new AntPathRequestMatcher(RAID_API + "/**", "PUT"))
                        .access(AuthorizationManagers.anyOf(
                                servicePointOwner(),
                                hasRaidPermissions(RAID_ADMIN_ROLE, ADMIN_RAIDS_CLAIM),
                                hasRaidPermissions(RAID_USER_ROLE, USER_RAIDS_CLAIM)
                        ))
                        .requestMatchers(new AntPathRequestMatcher(RAID_API + "/**", "PATCH"))
                        .access(AuthorizationManagers.anyOf(
                                hasRole(CONTRIBUTOR_WRITER_ROLE),
                                servicePointOwner()
                        ))
                        .requestMatchers(new AntPathRequestMatcher(RAID_API + "/**", "POST"))
                        .hasAnyRole(RAID_ADMIN_ROLE)
                        .requestMatchers("/contributor/**").hasRole(RAID_SEARCHER_ROLE)
                        .requestMatchers("/organisation/**").hasRole(RAID_SEARCHER_ROLE)
                        .requestMatchers(new AntPathRequestMatcher(RAID_API + "/**"))
                        .hasRole(SERVICE_POINT_USER_ROLE)
                        .requestMatchers(new AntPathRequestMatcher(SERVICE_POINT_API + "/**", "PUT"))
                        .hasRole(OPERATOR_ROLE)
                        .requestMatchers(new AntPathRequestMatcher(SERVICE_POINT_API + "/**", "POST"))
                        .hasRole(OPERATOR_ROLE)
                        .requestMatchers(new AntPathRequestMatcher(SERVICE_POINT_API + "/**", "GET"))
                        .hasRole(SERVICE_POINT_USER_ROLE)
                        .anyRequest().denyAll()
        );
        http.oauth2ResourceServer((oauth2) -> oauth2
                .jwt(Customizer.withDefaults()));
        http.oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/"));

        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            var authority = authorities.iterator().next();
            boolean isOidc = authority instanceof OidcUserAuthority;

            if (isOidc) {
                var oidcUserAuthority = (OidcUserAuthority) authority;
                var userInfo = oidcUserAuthority.getUserInfo();

                // Tokens can be configured to return roles under
                // Groups or REALM ACCESS hence have to check both
                if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) {
                    var realmAccess = userInfo.getClaimAsMap(REALM_ACCESS_CLAIM);
                    var roles = (Collection) realmAccess.get(ROLES_CLAIM);
                    roles.forEach(role -> log.debug((String) role));

                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                } else if (userInfo.hasClaim(GROUPS)) {
                    Collection roles = userInfo.getClaim(
                            GROUPS);
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            } else {
                var oauth2UserAuthority = (OAuth2UserAuthority) authority;
                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

                if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) {
                    Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get(REALM_ACCESS_CLAIM);

                    Collection roles = (Collection) realmAccess.get(ROLES_CLAIM);
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            }
            return mappedAuthorities;
        };
    }

    Collection<SimpleGrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
        Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
            Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
            Collection<String> roles = realmAccess.get("roles");
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        };

        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    private AuthorizationManager<RequestAuthorizationContext> servicePointOwner() {
        return (authentication, context) -> {
            final var token = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();

            if (token == null) {
                return new AuthorizationDecision(false);
            }

            if (!((List<?>) ((Map<?,?>) token.getClaim(REALM_ACCESS_CLAIM)).get(ROLES_CLAIM)).contains(SERVICE_POINT_USER_ROLE)) {
                return new AuthorizationDecision(false);
            }

            final var groupId = (String) token.getClaims().get(SERVICE_POINT_GROUP_ID_CLAIM);

            if (groupId == null) {
                return new AuthorizationDecision(false);
            }

            final var servicePoint = servicePointService.findByGroupId(groupId)
                    .orElseThrow(() -> new ServicePointNotFoundException(groupId));

            final var pathParts = context.getRequest().getRequestURI().split("/");

            if (pathParts.length < 4) {
                log.debug("Invalid path for permissions. Handle must be present {}", context.getRequest().getRequestURI());
                return new AuthorizationDecision(false);
            }

            final var handle = "%s/%s".formatted(pathParts[2], pathParts[3]);

            final var raid = raidHistoryService.findByHandle(handle)
                    .orElseThrow(() -> new ResourceNotFoundException(handle));

            if (raid.getIdentifier().getOwner().getServicePoint().equals(servicePoint.getId())) {
                return new AuthorizationDecision(true);
            }


            return new AuthorizationDecision(false);
        };
    }


    private AuthorizationManager<RequestAuthorizationContext> anyServicePointUserUnlessEmbargoed() {
        return (authentication, context) -> {
            final var token = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();

            if (token == null) {
                return new AuthorizationDecision(false);
            }

            final var pathParts = context.getRequest().getRequestURI().split("/");

            final var handle = "%s/%s".formatted(pathParts[2], pathParts[3]);

            final var raid = raidHistoryService.findByHandle(handle)
                    .orElseThrow(() -> new ResourceNotFoundException(handle));

            if (raid.getAccess().getType().getId().equals(SchemaValues.ACCESS_TYPE_EMBARGOED.getUri())) {
                return new AuthorizationDecision(false);
            }

            final var groupId = (String) token.getClaims().get(SERVICE_POINT_GROUP_ID_CLAIM);

            if (groupId == null) {
                return new AuthorizationDecision(false);
            }

            final var servicePoint = servicePointService.findByGroupId(groupId)
                    .orElseThrow(() -> new ServicePointNotFoundException(groupId));


            if (raid.getIdentifier().getOwner().getServicePoint().equals(servicePoint.getId())) {
                return new AuthorizationDecision(true);
            }

            return new AuthorizationDecision(false);
        };
    }

    private AuthorizationManager<RequestAuthorizationContext> hasRaidPermissions(final String roleName, final String claimName) {
        return (authentication, context) -> {
            final var token = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();

            if (token == null) {
                return new AuthorizationDecision(false);
            }

            if (!((List<?>) ((Map<?,?>) token.getClaim(REALM_ACCESS_CLAIM)).get(ROLES_CLAIM)).contains(roleName)) {
                return new AuthorizationDecision(false);
            }

            final var pathParts = context.getRequest().getRequestURI().split("/");

            if (pathParts.length < 4) {
                log.debug("Invalid path for permissions. Handle must be present {}", context.getRequest().getRequestURI());
                return new AuthorizationDecision(false);
            }

            final var handle = "%s/%s".formatted(pathParts[2], pathParts[3]);



            final var validRaids = (List<?>) token.getClaims().get(claimName);

            if (validRaids != null && validRaids.contains(handle)) {
                return new AuthorizationDecision(true);
            }

            return new AuthorizationDecision(false);
        };
    }

}