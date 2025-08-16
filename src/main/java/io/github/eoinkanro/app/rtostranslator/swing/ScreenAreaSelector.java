package io.github.eoinkanro.app.rtostranslator.swing;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class ScreenAreaSelector {

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

    SelectorOverlay selectorOverlay = new SelectorOverlay(future);
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

  @RequiredArgsConstructor
  private static class SelectorOverlay extends JPanel {

    private static final String START_HINT = "Click and drag to select • Right-click to cancel";
    private static final String PROCESS_HINT = "Drag to select • Right-click to cancel";
    private static final float HINT_FONT_SIZE = 16f;

    private static final BasicStroke SELECTOR_STROKE = new BasicStroke(2f);
    private static final Color HINT_BACKROUND_COLOR = new Color(0,0,0,120);
    private static final Color SELECTOR_COLOR = new Color(0, 120, 215, 80);
    private static final Color SELECTOR_BORDER_COLOR = new Color(0, 120, 215, 200);

    @NonNull
    private final CompletableFuture<Rectangle> resultFuture;

    private Point start;
    private Point end;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      Graphics2D gCopy = (Graphics2D) g.create();
      printSelectedArea(gCopy);
      printHint(gCopy);
      gCopy.dispose();
    }

    private void printSelectedArea(Graphics2D gCopy) {
      if (start == null || end == null) {
        return;
      }

      Rectangle rectangle = createRectangle(start, end);
      gCopy.setColor(SELECTOR_COLOR);
      gCopy.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

      gCopy.setStroke(SELECTOR_STROKE);
      gCopy.setColor(SELECTOR_BORDER_COLOR);
      gCopy.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    private void printHint(Graphics2D gCopy) {
      gCopy.setFont(getFont().deriveFont(Font.BOLD, HINT_FONT_SIZE));

      String hintText = start != null ? PROCESS_HINT : START_HINT;

      FontMetrics fontMetrics = gCopy.getFontMetrics();
      int textWidth = fontMetrics.stringWidth(hintText);
      int textHeight = fontMetrics.getHeight();

      int paddingX = fontMetrics.charWidth('M') / 2;
      int paddingY = textHeight / 2;

      int backgroundWidth = textWidth + paddingX;
      int backgroundHeight = textHeight + paddingY;

      int textStartX = paddingX + paddingX / 2;
      int textStartY = textHeight + paddingY;

      //draw background
      gCopy.setColor(HINT_BACKROUND_COLOR);
      gCopy.fillRect(paddingX, paddingY, backgroundWidth, backgroundHeight);

      //draw text
      gCopy.setColor(Color.WHITE);
      gCopy.drawString(hintText, textStartX, textStartY);
    }

    public void init() {
      setOpaque(false);

      addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          //close without result
          if (SwingUtilities.isRightMouseButton(e)) {
            submitResultAndClose(null);
            return;
          }

          start = e.getPoint();
          end = start;
          repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          Rectangle rectangle = createRectangle(start, e.getPoint());
          submitResultAndClose(rectangle);
        }
      });

      addMouseMotionListener(new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
          end = e.getPoint();
          repaint();
        }
      });
    }

    private Rectangle createRectangle(Point a, Point b) {
      int x = Math.min(a.x, b.x);
      int y = Math.min(a.y, b.y);
      int w = Math.abs(a.x - b.x);
      int h = Math.abs(a.y - b.y);
      return new Rectangle(x, y, w, h);
    }

    private void submitResultAndClose(Rectangle result) {
      resultFuture.complete(result);
      SwingUtilities.getWindowAncestor(this).dispose();
    }

  }

}
