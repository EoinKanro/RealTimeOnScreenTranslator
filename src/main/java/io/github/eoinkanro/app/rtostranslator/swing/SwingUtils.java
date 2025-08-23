package io.github.eoinkanro.app.rtostranslator.swing;

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class SwingUtils {

  public static void setIcon(JFrame frame) {
    File icon = new File(System.getProperty("user.dir") + File.separator + "icon.png");
    if (icon.exists()) {
      frame.setIconImage(new ImageIcon(icon.getAbsolutePath()).getImage());
    }
  }

}
