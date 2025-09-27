package io.github.eoinkanro.app.rtostranslator;

import io.github.eoinkanro.app.rtostranslator.ocr.OcrProvider;
import io.github.eoinkanro.app.rtostranslator.ocr.TesseractOcrProvider;
import io.github.eoinkanro.app.rtostranslator.ocr.WindowsOcrProvider;
import io.github.eoinkanro.app.rtostranslator.swing.ScreenAreaSelectorOverlay;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import net.sourceforge.tess4j.TesseractException;

public class ASd {

  public static void main(String[] args)
      throws URISyntaxException, AWTException, TesseractException, IOException {
    TesseractOcrProvider tesseractOcrProvider = new TesseractOcrProvider();
    WindowsOcrProvider windowsOcrProvider = new WindowsOcrProvider();

    ScreenAreaSelectorOverlay screenAreaSelectorOverlay = new ScreenAreaSelectorOverlay();
    Rectangle rectangle = screenAreaSelectorOverlay.selectScreenArea().join();
    BufferedImage image = screenAreaSelectorOverlay.captureScreen(rectangle);

    System.out.println("windows: " + windowsOcrProvider.getText(image));
    System.out.println("tesseract: " + tesseractOcrProvider.getText(image));

    rectangle = screenAreaSelectorOverlay.selectScreenArea().join();
    image = screenAreaSelectorOverlay.captureScreen(rectangle);

    System.out.println("windows: " + windowsOcrProvider.getText(image));
    System.out.println("tesseract: " + tesseractOcrProvider.getText(image));

    List.of(tesseractOcrProvider, windowsOcrProvider).forEach(OcrProvider::close);
  }

}
