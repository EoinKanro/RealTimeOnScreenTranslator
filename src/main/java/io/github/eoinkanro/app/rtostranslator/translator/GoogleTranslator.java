package io.github.eoinkanro.app.rtostranslator.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GoogleTranslator implements Translator {

  private static final String BASE_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=";

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final String preparedUrl;

  public GoogleTranslator(SettingsContext settingsContext) {
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
    this.preparedUrl = BASE_URL.formatted(
        settingsContext.getSourceLanguage().getGoogle(),
        settingsContext.getTargetLanguage().getGoogle());
  }

  @Override
  public String translate(String text) throws Exception {
    String url = preparedUrl + URLEncoder.encode(text, StandardCharsets.UTF_8);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    ArrayNode jsonNode = (ArrayNode) objectMapper.readValue(response.body(), ArrayNode.class)
        .get(0);
    StringBuilder result = new StringBuilder();
    jsonNode.forEach(node -> {
      result.append(node.get(0).asText());
      result.append(" ");
    });
    return result.toString();
  }

  @Override
  public void close() {
    httpClient.close();
  }

}
