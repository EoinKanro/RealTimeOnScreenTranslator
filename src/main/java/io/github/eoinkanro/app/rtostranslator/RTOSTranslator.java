package io.github.eoinkanro.app.rtostranslator;

import io.github.eoinkanro.app.rtostranslator.process.ApplicationProcessor;
import java.awt.AWTException;

public class RTOSTranslator {

  public static void main(String[] args) throws AWTException {
    new ApplicationProcessor().start();
  }

}
