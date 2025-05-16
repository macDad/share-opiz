import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HashUtils {

    public static String computeHmacHash(String password, String salt, int iterations, String algorithm) {
        try {
            // Convert legacy algo string to Java equivalent (e.g., "HMACSHA256")
            String macAlgorithm = algorithm.replace("HMAC", "Hmac");

            // Combine password + salt
            byte[] input = (password + salt).getBytes(StandardCharsets.UTF_8);

            // Initialize the HMAC
            Mac mac = Mac.getInstance(macAlgorithm);
            SecretKeySpec keySpec = new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), macAlgorithm);
            mac.init(keySpec);

            byte[] result = input;
            for (int i = 0; i < iterations; i++) {
                result = mac.doFinal(result);
            }

            // Convert to hex or base64, depending on how it's stored in legacy
            return bytesToHex(result); // or Base64.getEncoder().encodeToString(result)

        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC hash", e);
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
