package authenticate.service;

import authenticate.config.SecretManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthenticationService {

    private final CognitoIdentityProviderClient cognitoClient;

    public AuthenticationService() {
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

    }

    public Map<String, String> authenticateUser(String document, String senha) {
        var user = Optional.ofNullable(document);
        var password = Optional.ofNullable(senha);
        var secretsCognito = SecretManager.getCognitoSecrets();

        var authRequest = InitiateAuthRequest.builder()
                .clientId(secretsCognito.appClientId())
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(Map.of(
                        "USERNAME", user.orElse(secretsCognito.adminUser()),
                        "PASSWORD", password.orElse(secretsCognito.adminPassword())
                ))
                .build();

        var authResponse = cognitoClient.initiateAuth(authRequest);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", authResponse.authenticationResult().accessToken());
        responseBody.put("expiresIn",String.valueOf(authResponse.authenticationResult().expiresIn()));
        responseBody.put("refreshToken", authResponse.authenticationResult().refreshToken());

        return responseBody;
    }
}
