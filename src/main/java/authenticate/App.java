package authenticate;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import authenticate.service.AuthenticationService;

import java.util.HashMap;
import java.util.Map;


/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private final AuthenticationService authenticationService;

    public App() {
        this.authenticationService = new AuthenticationService();
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
//        LOGGER.info("Iniciando a requisição para gerar token, usuario ");
        var username = input.getQueryStringParameters() != null ? input.getQueryStringParameters().get("username") : null;
        var password = input.getQueryStringParameters() != null ? input.getQueryStringParameters().get("password") : null;

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        try {
            var token = authenticationService.authenticateUser(username, password);

            return response
                    .withStatusCode(200)
                    .withBody(token);
        } catch (RuntimeException e) {
//            LOGGER.error("Erro ao gerar token", e);
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }
}
