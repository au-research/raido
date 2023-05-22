This diagram outlines how the 
[RaidWebSecurityConfig](/api-svc/spring/src/main/java/raido/apisvc/spring/config/RaidWebSecurityConfig.java)
is bootstrapped and the `AuthenticationManagerResolver()` method links to the 
[RaidV2AuthenticationProvider](/api-svc/spring/src/main/java/raido/apisvc/spring/security/raidv2/RaidV2AuthenticationProvider.java).

See [oauth2_api-token_exchange.md](../authentication/oauth2_api-token_exchange.md)
for details about how an app-user signs in and the app-client obtains an 
api-token.

See [api-token-authz-flow.md](./api-token-authz-flow.md) for details
of how Spring and the `RaidV2AuthenticationProvider` work to implement secured
endpoint calls.

```mermaid
sequenceDiagram
autonumber
actor container as Container
participant java as Java
participant api as Api
participant webServer as EmbeddedJetty
participant spring as Spring framework
participant apiConfig as ApiConfig
participant securityConfig as RaidWebSecurityConfig

container->>java: java -jar raido-api-svc.jar
java->>api: main(String[] args)
api->>webServer: startJoin()
webServer->>spring: new WebApplicationContext().<br/>register(ApiConfig.class)
spring->>apiConfig: load components from<br/>@Bean methods
spring->>securityConfig: load components from<br/>@Bean methods
spring->>securityConfig: securityFilterChain()
securityConfig->>spring: http.requestMatchers("/v2/**").fullyAuthenticated()
securityConfig->>spring: http.oauth2ResourceServer(<br/>tokenAuthenticationManagerResolver() )
```
