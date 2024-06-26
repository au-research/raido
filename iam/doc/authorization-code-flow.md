# OAuth2 Authorization Code Flow in RAiD

## 1. Request an authorization code

The authorization code request requires the following parameters:
### client_id
The ID of the client assigned when the client was requested.
### response_type
This should be `code` for an authorization code request
### redirect_uri
The location the authorization server will redirect to with the authorization code. This needs to be provided to us in advance.
### scope
This must be provided as part of the OpenID Connect specification and must include the value `openid`
### nonce
This is optional but is recommended to mitigate replay attacks. For implementation details see https://openid.net/specs/openid-connect-core-1_0.html#NonceNotes
### state
This optional but is recommended to prevent CSRF attacks. See https://auth0.com/docs/secure/attack-protection/state-parameters#csrf-attacks
```
POST /realms/raid/protocol/openid-connect/auth
Host: iam.demo.raid.org.au
Content-Type: application/x-www-form-urlencoded

client_id=test-client
    &response_type=code
    &redirect_uri=https%3A%2F%2Fexample.org%2Fredirect-endpoint
    &scope=openid
    &nonce=n-0S6_WzA2Mj
    &state=af0ifjsldkj HTTP/1.1
```

## 2. User is authenticated and authorization code is returned
After authenticating the user, Keycloak will make a GET request to the `redirect_uri` specified in the initial request with the following parameters...
### iss
The issuer of the authorization code.
### session-state
A value used for session management in Keycloak
### state
If a state parameter was included with the initial request, the same value will be returned here.
### code
The authorization code returned from the authorization server

## 3. Request an access token
With the authorization code, we can now make a request to get an access token. We need to make a POST request with the following parameters...
### client_id
The ID of the client assigned when the client was requested
### client_secret
The password assigned to the client
### grant_type
This should be `authorization_code` as we want to exchange an authorization code for an access token.
### redirect_uri
This should be the same as the `redirect_uri` specified in the authorization code request.
### code
This should be the code returned from authorization code request 
```
POST /realms/raid/protocol/openid-connect/token
Host: iam.demo.raid.org.au
Content-Type: application/x-www-form-urlencoded

client_id=test-client
    &client_secret=xxx
    &grant_type=authorization_code
    &redirect_uri=https%3A%2F%2Fexample.org%2Fredirect-endpoint
    &code=b9aa9e39-d0be-44b7-917b-1c8e6971fb5e.df4c16e0-3d27-425a-9d01-3976eab310cb.2b95f19c-4ea1-4493-a8f7-c1797c4aa89a
```

## 4. An access token is returned
An access token is returned as part of the JSON response body. 

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJuZkMtdmoybkE4aGVEN25oMkFDeWI5Nk5ZYUVkTlZuQWVHV2x3bUw0bFEwIn0.eyJleHAiOjE3MTQ0NTQ4MDAsImlhdCI6MTcxNDQ1MzMwMCwiYXV0aF90aW1lIjoxNzE0NDUzMTQzLCJqdGkiOiJjNWQ1YjNhYi03NWViLTRlMGYtYmY0Zi1kYzQyZDAwZjg4NzciLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwMDEvcmVhbG1zL3JhaWQiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZmYzMzlhMjYtNzU0Yy00ZjlmLWIzY2EtYzRhN2ViOTNhMmEzIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidGVzdC1jbGllbnQiLCJub25jZSI6Inh4eCIsInNlc3Npb25fc3RhdGUiOiI1M2FhY2Y0YS0xZDFmLTQwYzMtYjI0NC0wYTk5ODZhZTViNjAiLCJhY3IiOiIwIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLXJhaWQiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCIsInNpZCI6IjUzYWFjZjRhLTFkMWYtNDBjMy1iMjQ0LTBhOTk4NmFlNWI2MCJ9.PF6_iGEs3sTwkQ1t_qix-e8JOLgK3k4WIzVvBraNF7FBjb8BhUCkXBaW9qZaZwUJebRHY7dMOEwa11uiXGootMoynbiBzsL2wZntXaoycLWL819P2mjWTd-xVmfRA09PlGTieaPJerod_sj6lmYAIV776i4kssOraOxT_B4ki3vYD777ISDx86idZzV7PdexUDf4V4dr4XhiUchX5MwEL5GfoSNBsW2O4fnOcojzoy1kr8MtHLY5gSMboYWv0Q1n_FWnQ0oLsvb33N1L_FJttzLqJ6M_H2Jfz8mURdgPCmrRG3MQXNm5WjaVAoYuO_JH0UiSn7rUwrCQylKzhTDAig",
  "expires_in": 1500,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzMDg0MGVhNi1hYjdiLTRmN2UtOGFlNS02MTI5Yzk1YTlhYzEifQ.eyJleHAiOjE3MTQ0NTUxMDAsImlhdCI6MTcxNDQ1MzMwMCwianRpIjoiYzQ1ZjI1M2UtYjdiNS00YTdjLWExZTMtNDY2MzVmZDgyYmEyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDAxL3JlYWxtcy9yYWlkIiwiYXVkIjoiaHR0cDovL2xvY2FsaG9zdDo4MDAxL3JlYWxtcy9yYWlkIiwic3ViIjoiZmYzMzlhMjYtNzU0Yy00ZjlmLWIzY2EtYzRhN2ViOTNhMmEzIiwidHlwIjoiUmVmcmVzaCIsImF6cCI6InRlc3QtY2xpZW50Iiwibm9uY2UiOiJ4eHgiLCJzZXNzaW9uX3N0YXRlIjoiNTNhYWNmNGEtMWQxZi00MGMzLWIyNDQtMGE5OTg2YWU1YjYwIiwic2NvcGUiOiJvcGVuaWQiLCJzaWQiOiI1M2FhY2Y0YS0xZDFmLTQwYzMtYjI0NC0wYTk5ODZhZTViNjAifQ.7zOxnQCXrFEN5eEU4kjFxxzMZbySJqyfMD9kf32pXlMVuxauUUbZ69JpK0v-IiueK8sr0KNVhzdgGphhBWjOOw",
  "token_type": "Bearer",
  "id_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJuZkMtdmoybkE4aGVEN25oMkFDeWI5Nk5ZYUVkTlZuQWVHV2x3bUw0bFEwIn0.eyJleHAiOjE3MTQ0NTQ4MDAsImlhdCI6MTcxNDQ1MzMwMCwiYXV0aF90aW1lIjoxNzE0NDUzMTQzLCJqdGkiOiJiMjA4YWU2ZC1mY2MxLTRjNmEtYjQ0ZS0zZDBjZTU4ZmFhOWMiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwMDEvcmVhbG1zL3JhaWQiLCJhdWQiOiJ0ZXN0LWNsaWVudCIsInN1YiI6ImZmMzM5YTI2LTc1NGMtNGY5Zi1iM2NhLWM0YTdlYjkzYTJhMyIsInR5cCI6IklEIiwiYXpwIjoidGVzdC1jbGllbnQiLCJub25jZSI6Inh4eCIsInNlc3Npb25fc3RhdGUiOiI1M2FhY2Y0YS0xZDFmLTQwYzMtYjI0NC0wYTk5ODZhZTViNjAiLCJhdF9oYXNoIjoiMVltVXhDRDFqUDhxLVlDMkd5R0pwQSIsImFjciI6IjAiLCJzaWQiOiI1M2FhY2Y0YS0xZDFmLTQwYzMtYjI0NC0wYTk5ODZhZTViNjAifQ.dpgGbWSjFDYTsMbrLSUahUtr9e5NipfJRr3NR9hMzdguqe8aWcwT1i6AsupFoFDcKoacDnjGt7LMGfT_3Cb0HnOFPE2rYmRjStK6cfIHTJCpqdl59h-f6I5aEaz5HitLh3-U8hCOqoMqdEqcb4gHpRbPMQ61uWuUnhqkv9p9_W14sBVsqg_E-RU21WTuHbvtDjBziaj7_wPg6dd96uA-COfb3mx0QAKQtnyQ-5bZ_YW8LrrHagq0w0Iezi9_biuUx0BTbF3hznm5blMAOMrGbFVRlFzFGs0qnTlPTyHl4Fgnie4XsjdWKP3gatt6yVb3TKcJZNgljoz_f2B6IbFmBA",
  "not-before-policy": 0,
  "session_state": "53aacf4a-1d1f-40c3-b244-0a9986ae5b60",
  "scope": "openid"
}
```

The access token can then be used in requests to the RAiD API by including it in the Authorization header...
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJuZkMtdmoybkE4aGVEN25oMkFDeWI5Nk5ZYUVkTlZuQWVHV2x3bUw0bFEwIn0.eyJleHAiOjE3MTQ0NTQ4MDAsImlhdCI6MTcxNDQ1MzMwMCwiYXV0aF90aW1lIjoxNzE0NDUzMTQzLCJqdGkiOiJjNWQ1YjNhYi03NWViLTRlMGYtYmY0Zi1kYzQyZDAwZjg4NzciLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwMDEvcmVhbG1zL3JhaWQiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZmYzMzlhMjYtNzU0Yy00ZjlmLWIzY2EtYzRhN2ViOTNhMmEzIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidGVzdC1jbGllbnQiLCJub25jZSI6Inh4eCIsInNlc3Npb25fc3RhdGUiOiI1M2FhY2Y0YS0xZDFmLTQwYzMtYjI0NC0wYTk5ODZhZTViNjAiLCJhY3IiOiIwIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLXJhaWQiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCIsInNpZCI6IjUzYWFjZjRhLTFkMWYtNDBjMy1iMjQ0LTBhOTk4NmFlNWI2MCJ9.PF6_iGEs3sTwkQ1t_qix-e8JOLgK3k4WIzVvBraNF7FBjb8BhUCkXBaW9qZaZwUJebRHY7dMOEwa11uiXGootMoynbiBzsL2wZntXaoycLWL819P2mjWTd-xVmfRA09PlGTieaPJerod_sj6lmYAIV776i4kssOraOxT_B4ki3vYD777ISDx86idZzV7PdexUDf4V4dr4XhiUchX5MwEL5GfoSNBsW2O4fnOcojzoy1kr8MtHLY5gSMboYWv0Q1n_FWnQ0oLsvb33N1L_FJttzLqJ6M_H2Jfz8mURdgPCmrRG3MQXNm5WjaVAoYuO_JH0UiSn7rUwrCQylKzhTDAig
```