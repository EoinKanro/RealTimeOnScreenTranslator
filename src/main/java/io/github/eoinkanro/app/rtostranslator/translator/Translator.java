package io.github.eoinkanro.app.rtostranslator.translator;

import java.io.Closeable;

public interface Translator extends Closeable {

  String translate(String text);

  default void close() {}

}
