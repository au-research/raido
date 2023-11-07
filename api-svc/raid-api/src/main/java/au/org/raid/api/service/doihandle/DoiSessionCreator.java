package au.org.raid.api.service.doihandle;

import java.util.Base64;

public class DoiSessionCreator {
    public String createSessionId() {
        try {
            byte[] privateKey = Base64.getDecoder().decode(System.getenv("DOI_PRIVATE_KEY"));

            String handleServer = System.getenv("DOI_HANDLE_SERVER");

            // CREATE SESSION
            DoiSessionInitiator doiSessionInitiator = new DoiSessionInitiator();
            DoiSessionInitiatorResponse doiSessionInitiatorResponse = doiSessionInitiator.initiate(handleServer);

            String sessionId = doiSessionInitiatorResponse.sessionId;
            String nonceEncoded = doiSessionInitiatorResponse.nonceEncoded;

            DoiSessionSignatureGenerator doiSessionSignatureGenerator = new DoiSessionSignatureGenerator();
            DoiSessionSignatureGeneratorResponse signatureResult = doiSessionSignatureGenerator.generateDualNonceSignature(nonceEncoded, privateKey);

            String signatureEncoded = signatureResult.signatureEncoded;
            String cnonceEncoded = signatureResult.cnonceEncoded;

            DoiSessionAuthenticator doiSessionAuthenticator = new DoiSessionAuthenticator();
            // AUTHENTICATE SESSION
            boolean authenticationResult = doiSessionAuthenticator.authenticateSession(sessionId, signatureEncoded, cnonceEncoded);

            if (!authenticationResult) {
                throw new Exception("Could not authenticate session.");
            }

            return sessionId;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

}

