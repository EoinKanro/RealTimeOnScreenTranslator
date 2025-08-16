package io.github.eoinkanro.app.rtostranslator.ocr;

import java.awt.image.BufferedImage;
import org.jspecify.annotations.Nullable;

public interface OcrProvider {

  //todo Language?
  @Nullable
  String getText(BufferedImage image);

  default void close() {}

}
