package io.github.eoinkanro.app.rtostranslator;

import io.github.eoinkanro.app.rtostranslator.ocr.TesseractOcrProvider;
import io.github.eoinkanro.app.rtostranslator.ocr.WindowsOcrProvider;
import io.github.eoinkanro.app.rtostranslator.settings.Language;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import io.github.eoinkanro.app.rtostranslator.swing.ScreenAreaSelectorOverlay;
import io.github.eoinkanro.app.rtostranslator.translator.OllamaTranslator;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import lombok.SneakyThrows;
import net.sourceforge.tess4j.TesseractException;

public class ASd {

  @SneakyThrows
  public static void main(String[] args)
      throws URISyntaxException, AWTException, TesseractException, IOException {
    TesseractOcrProvider tesseractOcrProvider = new TesseractOcrProvider();
    WindowsOcrProvider windowsOcrProvider = new WindowsOcrProvider();

    ScreenAreaSelectorOverlay screenAreaSelectorOverlay = new ScreenAreaSelectorOverlay();

    Rectangle rectangle = screenAreaSelectorOverlay.selectScreenArea().join();
    BufferedImage image = screenAreaSelectorOverlay.captureScreen(rectangle);

    String windowsText = tesseractOcrProvider.getText(image);
    String tesseractText = tesseractOcrProvider.getText(image);

    System.out.println("windows: " + windowsText);
    System.out.println("tesseract: " + tesseractText);

    OllamaTranslator translator = new OllamaTranslator(SettingsContext.builder()
        .sourceLanguage(Language.ENGLISH)
        .targetLanguage(Language.RUSSIAN)
        .build());

    System.out.println("windows translate: " + translator.translate(windowsText));
    System.out.println("tesseract translate: " + translator.translate(tesseractText));

    List.of(tesseractOcrProvider, windowsOcrProvider, translator).forEach(it -> {
      try {
        it.close();
      } catch (Exception e) {

      }
    });
  }

}
