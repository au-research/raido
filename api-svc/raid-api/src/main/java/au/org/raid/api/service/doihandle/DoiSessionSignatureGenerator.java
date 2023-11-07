package au.org.raid.api.service.doihandle;

import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

class DoiSessionSignatureGeneratorResponse {
    public String signatureEncoded;
    public String cnonceEncoded;
}

@Service
public class DoiSessionSignatureGenerator {
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public DoiSessionSignatureGeneratorResponse generateDualNonceSignature(String nonceEncoded, byte[] privateKeyDecoded) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        if (privateKeyDecoded.length == 0) {
            throw new IllegalArgumentException("No private key specified");
        }

        DoiSessionSignatureGeneratorResponse result = new DoiSessionSignatureGeneratorResponse();

        // Load the private key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDecoded));

        // Decode the nonce from base64
        byte[] nonceDecoded = Base64.getDecoder().decode(nonceEncoded);

        // Create a client nonce
        byte[] cnonce = new byte[16];
        SECURE_RANDOM.nextBytes(cnonce);

        // Sign the concatenated nonce + cnonce with the private key
        Signature signatureInstance = Signature.getInstance("SHA256withRSA");
        signatureInstance.initSign(privateKey);
        signatureInstance.update(nonceDecoded);
        signatureInstance.update(cnonce);
        byte[] signature = signatureInstance.sign();

        // Encode the signature and cnonce in base64
        result.signatureEncoded = BASE64_ENCODER.encodeToString(signature);
        result.cnonceEncoded = BASE64_ENCODER.encodeToString(cnonce);

        return result;
    }

}
