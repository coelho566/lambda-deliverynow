package authenticate.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

public class SecretManager {

    public static CognitoSecrets getCognitoSecrets() {
        var objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(getSecret(), CognitoSecrets.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getSecret() {

        var secretName = "dev/cognito_secrets";

        var client = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();

        var getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        var getSecretValueResponse = client.getSecretValue(getSecretValueRequest);

        return getSecretValueResponse.secretString();
    }
}
