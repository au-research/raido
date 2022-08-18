package raido

class EcrUtil {
  static String getEcrLoginPassword(String repoName) {

    /*
  AmazonECRClient createNewEcrClientForRegion() {
    AmazonECRClient ecrClient =
      new AmazonECRClient(
        aws.createProviderChain(accessKey, secretKey),
        aws.clientConfiguration )
    ecrClient.setRegion(Region.getRegion(aws.defaultRegion))
    return ecrClient

  GetAuthorizationTokenResult getAuthorizationToken()
  {
    if( dryRun ){
      println "*** DRY RUN - skipped get auth token "
      return new GetAuthorizationTokenResult()
    }

    return ecr.getAuthorizationToken(
      new GetAuthorizationTokenRequest())
  }

    String getAuthorizationToken(
      EcrClientWrapper ecr = getCachedEcrWrapper(),
      String registryName)
    {
      GetAuthorizationTokenResult authToken = ecr.authorizationToken
      assert authToken.authorizationData.size() == 1
      AuthorizationData authData = authToken.authorizationData[0]
      assert authData.proxyEndpoint == "https://$registryName"
      // This comes back in <user>:<password> format
      String decodedToken = new String(Base64.decoder.decode(
        authData.authorizationToken ))
      // "AWS" is always the username
      assert decodedToken.startsWith("AWS:")
      return decodedToken - "AWS:"
    }

    */
    return ""
  }
}
