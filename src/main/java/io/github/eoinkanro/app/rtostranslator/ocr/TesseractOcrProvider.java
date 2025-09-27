package io.github.eoinkanro.app.rtostranslator.ocr;

import java.awt.image.BufferedImage;
import java.io.File;

import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import io.github.eoinkanro.app.rtostranslator.utils.LogUtils;
import net.sourceforge.tess4j.Tesseract;
import org.jspecify.annotations.Nullable;

public class TesseractOcrProvider implements OcrProvider {

  private final Tesseract tesseract;

  public TesseractOcrProvider(SettingsContext settingsContext) {
    tesseract = new Tesseract();
    String data = System.getProperty("user.dir") + File.separator + "tesseract";
    tesseract.setDatapath(data);
    tesseract.setLanguage(settingsContext.getSourceLanguage().getTesseract());
  }

  @Nullable
  @Override
  public String getText(BufferedImage image) {
    try {
      return tesseract.doOCR(image);
    } catch (Exception e) {
        LogUtils.logError(e);
    }
    return null;
  }

}
