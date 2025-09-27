package io.github.eoinkanro.app.rtostranslator.settings;

import lombok.*;

@Setter
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettingsContext {

  @Builder.Default
  private Language sourceLanguage = Language.ENGLISH;
  @Builder.Default
  private Language targetLanguage = Language.RUSSIAN;

  @Builder.Default
  private long updatePeriodMs = 2000;

  @Builder.Default
  private OcrEngine ocrEngine = OcrEngine.TESSERACT_OCR;

  @Builder.Default
  private TranslatorEngine translatorEngine = TranslatorEngine.OLLAMA;
  @Builder.Default
  private String translatorModel = "gemma3:4b";
  @Builder.Default
  private String translatorAddress = "localhost";
  @Builder.Default
  private int translatorPort = 11434;
  @Builder.Default
  private String translatorPrompt = "You are a translation engine.\n"
      + "Input: a JSON object with three fields:  \n"
      + "* \"sourceLanguage\": the language of the input text  \n"
      + "* \"targetLanguage\": the language to translate into  \n"
      + "* \"text\": the text to translate  \n"
      + "\n"
      + "You should:\n"
      + "* Translate ONLY the value of \"text\" from \"sourceLanguage\" to \"targetLanguage\".\n"
      + "* Output ONLY the translated text as PLAIN TEXT.\n"
      + "* Do NOT output JSON, explanations, comments, formatting, or additional content.\n"
      + "\n"
      + "Input JSON: ";

}
