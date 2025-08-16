package io.github.eoinkanro.app.rtostranslator.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import org.jspecify.annotations.Nullable;

public class TesseractOcrProvider implements OcrProvider {

  private final Tesseract tesseract;

  public TesseractOcrProvider() {
    tesseract = new Tesseract();
    //todo
    String data = System.getProperty("user.dir") + File.separator + "tesseract";
    System.out.println(data);
    tesseract.setDatapath(data);
    tesseract.setLanguage("eng");
  }

  @Nullable
  @Override
  public String getText(BufferedImage image) {
    try {
      return tesseract.doOCR(image);
    } catch (Exception e) {
      //todo
    }
    return null;
  }

}
