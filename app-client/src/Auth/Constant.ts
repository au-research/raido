
// https://aaf.freshdesk.com/support/solutions/articles/19000096640-openid-connect-
export const aaf = {
  authorize: "https://central.test.aaf.edu.au/providers/op/authorize",
  issuer: "https://central.test.aaf.edu.au",
  token: "https://central.test.aaf.edu.au/providers/op/token",
  jwks: "https://central.test.aaf.edu.au/providers/op/jwks",
  userInfo: "https://central.test.aaf.edu.au/providers/op/userinfo",
  //authnScope: "openid profile email",
  authnScope: "openid profile email" +
    " aueduperson eduperson_orcid eduperson_assurance eduperson_affiliation" +
    " eduperson_entitlement schac_home_organization",
}

export const google = {
  authorize: "https://accounts.google.com/o/oauth2/v2/auth",
  issuer: "https://accounts.google.com",
  jwks: "https://www.googleapis.com/oauth2/v3/certs",
  token: "https://oauth2.googleapis.com/token",
  authnScope: "openid email",
}

export const oauthCodeGrantFlow = "code";
