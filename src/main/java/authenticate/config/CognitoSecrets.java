package authenticate.config;

public record CognitoSecrets(
        String appClientId,
        String userPoolId,
        String adminUser,
        String adminPassword
) {
}
