package io.github.eoinkanro.app.rtostranslator.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum OcrEngine {

  WINDOWS_OCR("Windows OCR"),
  TESSERACT_OCR("Tesseract"),
  ;

  private final String displayName;

  @Nullable
  public static OcrEngine fromDisplayName(String displayName) {
    for (OcrEngine value : OcrEngine.values()) {
      if (value.getDisplayName().equals(displayName)) {
        return value;
      }
    }
    return null;
  }

}
