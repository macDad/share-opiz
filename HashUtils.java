import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HashUtil {

    public static String computeHmacHash(String password, String salt, int iterations, String algorithm) {
        try {
            String macAlgorithm = algorithm.replace("HMAC", "Hmac"); // e.g., "HMACSHA256" → "HmacSHA256"

            // Use salt as the HMAC key
            Mac mac = Mac.getInstance(macAlgorithm);
            SecretKeySpec keySpec = new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), macAlgorithm);
            mac.init(keySpec);

            byte[] result = password.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < iterations; i++) {
                result = mac.doFinal(result);
            }

            return bytesToHex(result);
        } catch (Exception e) {
            throw new RuntimeException("Error computing legacy HMAC hash", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
