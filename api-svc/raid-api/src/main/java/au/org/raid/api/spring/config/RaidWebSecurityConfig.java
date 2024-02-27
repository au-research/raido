package au.org.raid.api.spring.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RaidWebSecurityConfig {
    private static final String USER_ROLE = "service-point-user";
    private static final String GROUPS = "groups";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";

    private final KeycloakLogoutHandler keycloakLogoutHandler;
    public static final String RAID_V2_API = "/v2";
    public static final String RAID_STABLE_API = "/raid";
    public static final String SERVICE_POINT_API = "/service-point";
    public static final String TEAM_API = "/team";

    public static boolean isStableApi(HttpServletRequest request) {
        return request.getServletPath().startsWith(RAID_STABLE_API) ||
                request.getServletPath().startsWith(SERVICE_POINT_API) ||
                request.getServletPath().startsWith(TEAM_API);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui*/**").permitAll()
                        .requestMatchers("/docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v2/public/list-service-point/v1").permitAll()

                        .requestMatchers(new AntPathRequestMatcher(RAID_V2_API + "/**"))
                        .hasRole(USER_ROLE)

                        .requestMatchers(new AntPathRequestMatcher(RAID_STABLE_API + "/**"))
                        .hasRole(USER_ROLE)

                        .requestMatchers(new AntPathRequestMatcher(SERVICE_POINT_API + "/**"))
                        .hasRole(USER_ROLE)

                        .anyRequest().denyAll()
        );
        http.oauth2ResourceServer((oauth2) -> oauth2
                .jwt(Customizer.withDefaults()));
        http.oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/"));





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

//    @Bean
//    public AuthenticationManagerResolver<HttpServletRequest>
//    tokenAuthenticationManagerResolver(
//            RaidV2AuthenticationProvider raidV2AuthProvider
//    ) {
//        return (request) -> {
//            if (isRaidV2Api(request)) {
//                return raidV2AuthProvider::authenticate;
//            } else if (isStableApi(request)) {
//                return raidV2AuthProvider::authenticate;
//            } else {
//        /* client has done a request (probably a POST), with a bearer token,
//        but not on a recognised "API path".
//        IMPROVE: dig out the token and decode it, so we can log details? */
//                log.info("ignored bearer token authenticated request %s:%s:%s",
//                        request.getHeader(HOST), request.getMethod(), request.getRequestURI());
//                throw ExceptionUtil.authFailed();
//            }
//        };
//    }

    // maybe AuthnProvider can just be @Components now instead of explicit beans?
//    @Bean
//    public RaidV2AuthenticationProvider raidV2AuthProvider(
//            RaidV2AppUserApiTokenService appUserApiTokenSvc,
//            RaidV2ApiKeyApiTokenService apiKeyApiTokenSvc
//    ) {
//        return new RaidV2AuthenticationProvider(
//                appUserApiTokenSvc, apiKeyApiTokenSvc);
//    }

//    @Bean
//    public RequestRejectedHandler requestRejectedHandler() {
//    /* sends an error response with a configurable status code (default is 400
//     BAD_REQUEST) we can pass a different value in the constructor. */
//        return new HttpStatusRequestRejectedHandler() {
//            @Override
//            public void handle(
//                    HttpServletRequest request,
//                    HttpServletResponse response,
//                    RequestRejectedException ex
//            ) throws IOException {
//        /* i don't think we want a stack trace here?
//        user/principal stuff will not be populated because this tends to happen
//        very early in request processing. */
//                log.with("method", request.getMethod()).
//                        with("uri", request.getRequestURI()).
//                        with("params", request.getParameterMap()).
//                        with("message", ex.getMessage()).
//                        info("Request rejected");
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//            }
//        };
//    }

    /**
     * If encoded slashes aren't allowed, then calls of the form:
     * `http://localhost:8080/v1/handle/102.100.100%2F75517`
     * would get rejected by HttpStrictFirewall.  Which is unfortunate because
     * handles contain slashes as defined in the ISO standard.
     * Most client-technologies (Feign, RestTemplate, openapi-fetch, etc.) will, by
     * default, percent-encode data that is passed to them as a "parameter value".
     * 😢
     */
//    @Bean
//    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
//        log.info("allowUrlEncodedSlashHttpFirewall()");
//        StrictHttpFirewall firewall = new StrictHttpFirewall();
//        firewall.setAllowUrlEncodedSlash(true);
//        return firewall;
//    }

}