package io.github.eoinkanro.app.rtostranslator.swing.chat;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BottomResizeMouseAdapter extends MouseAdapter {

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
  public void mousePressed(MouseEvent e) {
    resizingPoint.set(e.getLocationOnScreen());
    resizingBounds.set(new Rectangle(mainFrame.getBounds()));
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    resizingPoint.set(null);
    resizingBounds.set(null);
  }

}
