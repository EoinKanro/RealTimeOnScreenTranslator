package io.github.eoinkanro.app.rtostranslator.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class SettingsContext {

  @Builder.Default
  private Language sourceLanguage = Language.ENGLISH;
  @Builder.Default
  private Language targetLanguage = Language.ENGLISH;

  @Builder.Default
  private long updatePeriodMs = 1000;

  @Builder.Default
  private OcrEngine ocrEngine = OcrEngine.TESSERACT_OCR;

  @Builder.Default
  private TranslatorEngine translatorEngine = TranslatorEngine.OLLAMA;
  @Builder.Default
  private String translatorModel = "qwen3:4b";
  @Builder.Default
  private String translatorAddress = "localhost";
  @Builder.Default
  private int translatorPort = 11434;
  @Builder.Default
  private String translatorPrompt = "My prompt";

}
