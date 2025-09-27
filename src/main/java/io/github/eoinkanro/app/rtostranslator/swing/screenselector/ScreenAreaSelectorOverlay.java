package io.github.eoinkanro.app.rtostranslator.swing.screenselector;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class ScreenAreaSelectorOverlay {

  private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 50);

  private Robot robot;

  /**
   * ATTENTION: not thread safe
   */
  public BufferedImage captureScreen(Rectangle rect) throws AWTException {
    if (robot == null) {
      robot = new Robot();
    }

    return robot.createScreenCapture(rect);
  }

  public CompletableFuture<Rectangle> selectScreenArea() {
    CompletableFuture<Rectangle> future = new CompletableFuture<>();
    SwingUtilities.invokeLater(() -> processSelection(future));
    return future;
  }

  private void processSelection(CompletableFuture<Rectangle> future) {
    Rectangle allScreens = getAllScreensArea();

    JWindow overlay = new JWindow();
    overlay.setAlwaysOnTop(true);
    overlay.setBounds(allScreens);
    overlay.setBackground(OVERLAY_COLOR);
    overlay.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

    ScreenAreaSelectorPanel selectorOverlay = new ScreenAreaSelectorPanel(future);
    selectorOverlay.init();

    overlay.setContentPane(selectorOverlay);
    overlay.setVisible(true);
    selectorOverlay.requestFocusInWindow();
  }

  private Rectangle getAllScreensArea() {
    Rectangle allScreens = new Rectangle();

    for (GraphicsDevice screen : GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getScreenDevices()) {
      Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
      allScreens = allScreens.union(screenBounds);
    }

    return allScreens;
  }

}
