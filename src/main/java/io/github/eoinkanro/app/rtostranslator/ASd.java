package io.github.eoinkanro.app.rtostranslator;

import io.github.eoinkanro.app.rtostranslator.swing.ScreenAreaSelector;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ASd {

  public static void main(String[] args) throws URISyntaxException, AWTException, TesseractException {
    Tesseract tesseract = new Tesseract();

    String data = Paths.get(ASd.class.getClassLoader().getResource("test2").toURI())
        .toAbsolutePath().toString();
    tesseract.setDatapath(data);
    tesseract.setLanguage("eng");

    ScreenAreaSelector screenAreaSelector = new ScreenAreaSelector();
    Rectangle rectangle = screenAreaSelector.selectScreenArea().join();
    BufferedImage image = screenAreaSelector.captureScreen(rectangle);

    String endlishText = tesseract.doOCR(image);

    System.out.println(endlishText);
  }

}
