package io.github.eoinkanro.app.rtostranslator.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

public class OllamaTranslator implements Translator {

  private static final String URI_TEMPLATE = "http://%s:%d/api/generate";
  private static final String MODEL_PARAM = "model";
  private static final String PROMPT_PARAM = "prompt";
  private static final String STREAM_PARAM = "stream";
  private static final String SOURCE_LANGUAGE_PARAM = "sourceLanguage";
  private static final String TARGET_LANGUAGE_PARAM = "targetLanguage";
  private static final String TEXT_PARAM = "text";
  private static final String RESPONSE_PARAM = "response";

  private static final String THINK_TAG = "</think>";

  private final SettingsContext settingsContext;
  private final HttpClient httpClient;
  private final URI address;

  private final ObjectMapper objectMapper;
  private final ObjectNode requestTemplate;
  private final ObjectNode promptTemplate;

  public OllamaTranslator(SettingsContext settingsContext) {
    this.settingsContext = settingsContext;

    this.httpClient = HttpClient.newHttpClient();
    this.address = URI.create(URI_TEMPLATE.formatted(settingsContext.getTranslatorAddress(), settingsContext.getTranslatorPort()));

    this.objectMapper = new ObjectMapper();
    requestTemplate = objectMapper.createObjectNode();
    requestTemplate.put(MODEL_PARAM, settingsContext.getTranslatorModel());
    requestTemplate.put(STREAM_PARAM, false);

    this.promptTemplate = objectMapper.createObjectNode();
    promptTemplate.put(SOURCE_LANGUAGE_PARAM, settingsContext.getSourceLanguage().getDisplayName());
    promptTemplate.put(TARGET_LANGUAGE_PARAM, settingsContext.getTargetLanguage().getDisplayName());
  }

  @Override
  public String translate(String text) {
    try {
      String body = objectMapper.writeValueAsString(createRequestBody(text));
      HttpRequest httpRequest = HttpRequest.newBuilder()
          .uri(address)
          .header("Content-Type", "application/json; charset=UTF-8")
          .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
          .build();

      HttpResponse<String> response = httpClient.send(httpRequest, BodyHandlers.ofString(StandardCharsets.UTF_8));
      return parseResponse(response.body());
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  private ObjectNode createRequestBody(String toTranslate) throws JsonProcessingException {
    ObjectNode prompt = promptTemplate.deepCopy();
    prompt.put(TEXT_PARAM, toTranslate);

    ObjectNode request = requestTemplate.deepCopy();
    request.put(PROMPT_PARAM, settingsContext.getTranslatorPrompt() + objectMapper.writeValueAsString(prompt));

    return request;
  }

  private String parseResponse(String response) throws JsonProcessingException {
    ObjectNode responseJson = objectMapper.readValue(response, ObjectNode.class);

    String resultString = responseJson.get(RESPONSE_PARAM).asText();
    String[] result = resultString.split(THINK_TAG);
    if (result.length > 1) {
      return result[result.length - 1];
    }

    return resultString;
  }

  public void close() {
    httpClient.close();
  }

}
