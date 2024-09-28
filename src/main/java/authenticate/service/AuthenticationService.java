package authenticate.service;

import authenticate.config.SecretManager;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;

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

    public String authenticateUser(String documento, String senha) {
        var user = Optional.ofNullable(documento);
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
        return authResponse.authenticationResult().idToken();
    }
}
