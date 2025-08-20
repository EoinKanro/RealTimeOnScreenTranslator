package io.github.eoinkanro.app.rtostranslator.ocr;

import io.github.eoinkanro.app.rtostranslator.utils.LogUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.imageio.ImageIO;

public abstract class FileOcrProvider implements OcrProvider {

  public String getText(BufferedImage image) {
    try {
      File tmpFile = Files.createTempFile("ocrImage", ".png").toFile();
      ImageIO.write(image, "png", tmpFile);
      String result = getText(tmpFile);
      tmpFile.delete();
      return result;
    } catch (Exception e) {
        LogUtils.logError(e);
    }
    return null;
  }

  protected abstract String getText(File imageFile) throws IOException;

}
