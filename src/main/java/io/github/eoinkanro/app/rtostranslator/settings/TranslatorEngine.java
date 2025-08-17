package io.github.eoinkanro.app.rtostranslator.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum TranslatorEngine {

  OLLAMA("Ollama"),
  GOOGLE("Google"),
  ;

  private final String displayName;

  @Nullable
  public static TranslatorEngine fromDisplayName(String displayName) {
    for (TranslatorEngine value : TranslatorEngine.values()) {
      if (value.getDisplayName().equals(displayName)) {
        return value;
      }
    }
    return null;
  }

}
