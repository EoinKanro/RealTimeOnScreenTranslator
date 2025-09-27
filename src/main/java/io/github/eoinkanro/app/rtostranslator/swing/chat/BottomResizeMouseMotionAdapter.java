package io.github.eoinkanro.app.rtostranslator.swing.chat;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class BottomResizeMouseMotionAdapter extends MouseMotionAdapter {

  private static final int BORDER_SIZE = 7;
  private static final int MIN_WIDTH = 300;
  private static final int MIN_HEIGHT = 300;

  /**
   * Frame that will be resized
   */
  private final JFrame mainFrame;
  /**
   * Reference to a point where resizing was clicked
   */
  private final AtomicReference<Point> resizingPoint;
  /**
   * Reference to bounds of frame before resizing
   */
  private final AtomicReference<Rectangle> resizingBounds;

  @Override
  public void mouseMoved(MouseEvent e) {
    Point frameLocation = mainFrame.getLocationOnScreen();
    Point mouseLocation = e.getLocationOnScreen();
    int x = mouseLocation.x - frameLocation.x;
    int y = mouseLocation.y - frameLocation.y;
    int width = mainFrame.getWidth();
    int height = mainFrame.getHeight();

    int cursor = Cursor.DEFAULT_CURSOR;
    if (x < BORDER_SIZE && y > height - BORDER_SIZE) {
      cursor = Cursor.SW_RESIZE_CURSOR;
    }
    else if (x > width - BORDER_SIZE && y > height - BORDER_SIZE) {
      cursor = Cursor.SE_RESIZE_CURSOR;
    }
    else if (x < BORDER_SIZE) {
      cursor = Cursor.W_RESIZE_CURSOR;
    }
    else if (x > width - BORDER_SIZE) {
      cursor = Cursor.E_RESIZE_CURSOR;
    }
    else if (y > height - BORDER_SIZE) {
      cursor = Cursor.S_RESIZE_CURSOR;
    }

    mainFrame.setCursor(Cursor.getPredefinedCursor(cursor));
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    Point startPoint = resizingPoint.get();
    Rectangle originalBounds = resizingBounds.get();

    if (startPoint == null || originalBounds == null) {
      return;
    }

    Point currentPoint = e.getLocationOnScreen();
    int dx = currentPoint.x - startPoint.x;
    int dy = currentPoint.y - startPoint.y;

    Rectangle bounds = new Rectangle(originalBounds);

    switch (mainFrame.getCursor().getType()) {
      case Cursor.SW_RESIZE_CURSOR -> {
        bounds.x += dx;
        bounds.width -= dx;
        bounds.height += dy;
      }
      case Cursor.SE_RESIZE_CURSOR -> {
        bounds.width += dx;
        bounds.height += dy;
      }
      case Cursor.W_RESIZE_CURSOR -> {
        bounds.x += dx;
        bounds.width -= dx;
      }
      case Cursor.E_RESIZE_CURSOR -> {
        bounds.width += dx;
      }
      case Cursor.S_RESIZE_CURSOR -> {
        bounds.height += dy;
      }
    }

    if (bounds.width >= MIN_WIDTH && bounds.height >= MIN_HEIGHT) {
      mainFrame.setBounds(bounds);
    }
  }

}
