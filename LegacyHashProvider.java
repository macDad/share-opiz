import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.policy.PasswordPolicy;

import java.util.List;

public class LegacyHashProvider implements PasswordHashProvider {

    private final String providerId = "legacy-hash-provider";
    private final PasswordPolicy defaultPolicy;

    public LegacyHashProvider(PasswordPolicy defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    @Override
    public boolean verify(RealmModel realm, UserModel user, CredentialModel credentialInput, String rawPassword) {
        if (!(credentialInput instanceof PasswordCredentialModel)) return false;

        List<String> legacyHash = user.getAttribute("legacy_hash");

        if (legacyHash != null && !legacyHash.isEmpty()) {
            // First login with legacy hash
            String storedHash = legacyHash.get(0);
            String salt = user.getFirstAttribute("legacy_salt");
            int iterations = Integer.parseInt(user.getFirstAttribute("legacy_iterations"));
            String algorithm = user.getFirstAttribute("legacy_algorithm");

            String computed = HashUtil.computeHmacHash(rawPassword, salt, iterations, algorithm);

            if (computed.equals(storedHash)) {
                // ✅ Password match → upgrade to Keycloak native hash
                PasswordCredentialModel newCred = PasswordCredentialModel.createFromValues(realm, credentialInput.getType(), rawPassword, this);
                user.credentialManager().updateStoredCredential(newCred);

                // Optional: set a flag instead of removing
                user.setSingleAttribute("legacy_migrated", "true");

                return true;
            } else {
                return false;
            }
        }

        // No legacy attributes → fallback to standard password check
        return false;
    }

    @Override
    public CredentialModel encode(String rawPassword, int iterations) {
        return PasswordCredentialModel.createFromValues(providerId, rawPassword, iterations, null);
    }

    @Override
    public boolean policyCheck(PasswordPolicy policy, CredentialModel credential) {
        return true; // Customize if needed
    }

    @Override
    public String getId() {
        return providerId;
    }

    @Override
    public void close() {
        // Cleanup if needed
    }
}
