package authenticate;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import authenticate.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    //    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private final AuthenticationService authenticationService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public App() {
        this.authenticationService = new AuthenticationService();
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        context.getLogger().log("Iniciando a requisição para gerar token, usuario");
        var username = input.getQueryStringParameters() != null ? input.getQueryStringParameters().get("username") : null;
        var password = input.getQueryStringParameters() != null ? input.getQueryStringParameters().get("password") : null;

        try {
            var token = authenticationService.authenticateUser(username, password);
            String jsonResponse = objectMapper.writeValueAsString(token);
            response.withStatusCode(200)
                    .withBody(jsonResponse);
        } catch (Exception e) {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("errorMessage", "Erro de autenticação: " + e.getMessage());

            try {
                String jsonError = objectMapper.writeValueAsString(errorBody);
                response.setStatusCode(400);
                response.setBody(jsonError);
            } catch (JsonProcessingException jsonException) {
                response.setStatusCode(500);
                response.setBody("{\"errorMessage\": \"Erro interno ao processar JSON.\"}");
            }
        }

        return response;
    }
}
