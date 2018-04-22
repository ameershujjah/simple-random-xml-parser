package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ParserException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import managers.ConversionManager;
import model.LambdaResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ameer on 4/22/18.
 */
@Log4j
public class ConversionRequestStreamHandler implements RequestStreamHandler {
    final private ObjectMapper objectMapper = new ObjectMapper();
    final private ConversionManager conversionManager = new ConversionManager();

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        final JSONObject request = getJSONFromStream(inputStream);
        final String requestUrls = ((JSONObject) request.get("queryStringParameters")).get("urls").toString();
        final String[] urls = requestUrls.split(",");
        final List<String> urlsList = Arrays.asList(urls);
        String responseBody = "";
        try {
            responseBody += conversionManager.convertAllAndReturnString(urlsList);
        } catch (ParserException e) {
            responseBody += "Error when trying to convert show xml to json, please contact the admin";
            log.error("Error when trying to parse show xml to json. For url list " + urlsList.toString() + " Error : " + e.getLocalizedMessage());
        }
        final LambdaResponse lambdaResponse = new LambdaResponse();
        lambdaResponse.setBody(responseBody);
        lambdaResponse.setStatusCode(200);
        lambdaResponse.setIsBase64Encoded(false);
        writeResponseToStream(lambdaResponse, outputStream);
    }

    private JSONObject getJSONFromStream(@NonNull final InputStream inputStream) throws IOException {
        try {
            final JSONParser jsonParser = new JSONParser();
            return (JSONObject) jsonParser.parse(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (ParseException e ){
            log.error("Error trying to parse JSON from inputStream");
            throw new IOException();
        }
    }

    private void writeResponseToStream(@NonNull final LambdaResponse lambdaResponse,
                                       @NonNull final OutputStream outputStream) throws IOException{
        final String lambdaResponseString = objectMapper.writeValueAsString(lambdaResponse);
        outputStream.write(lambdaResponseString.getBytes(StandardCharsets.UTF_8));
    }
}
