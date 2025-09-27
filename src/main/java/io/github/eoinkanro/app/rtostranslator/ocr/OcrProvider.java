package io.github.eoinkanro.app.rtostranslator.ocr;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import org.jspecify.annotations.Nullable;

public interface OcrProvider extends Closeable {

  @Nullable
  String getText(BufferedImage image);

  default void close() {}

}
