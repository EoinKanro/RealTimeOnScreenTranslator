package io.github.eoinkanro.app.rtostranslator.swing.chat;

import io.github.eoinkanro.app.rtostranslator.process.message.Message;
import io.github.eoinkanro.app.rtostranslator.process.message.OpenSettingsMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.SelectAreaMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.StartStopMessage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import javax.swing.JPanel;

class NavigationBar extends JPanel {

  private static final Color BACKGROUND = new Color(0, 0, 0, 229);

  private static final String START_TEXT = "Start";
  private static final String STOP_TEXT = "Stop";

  private final MenuButton startStopButton;
  private final AtomicReference<Point> draggingPoint;

  public NavigationBar(JFrame mainFrame, BlockingQueue<Message> output) {
    this.draggingPoint = new AtomicReference<>();

    setLayout(new BorderLayout());
    setBackground(BACKGROUND);

    //------- Close ---------
    MenuButton closeButton = new MenuButton("X");
    closeButton.addActionListener(e -> System.exit(0));
    add(closeButton, BorderLayout.EAST);

    JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    actionButtons.setOpaque(false);

    //-------- Start / Stop ---------
    startStopButton = new MenuButton(START_TEXT);
    startStopButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        output.add(new StartStopMessage());
      }
    });
    actionButtons.add(startStopButton);

    //--------- Settings --------
    MenuButton settingsButton = new MenuButton("Settings");
    settingsButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        output.add(new OpenSettingsMessage());
      }
    });
    actionButtons.add(settingsButton);

    //----------- Area -------------
    MenuButton areaButton = new MenuButton("Area");
    areaButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        output.add(new SelectAreaMessage());
      }
    });
    actionButtons.add(areaButton);

    add(actionButtons, BorderLayout.WEST);

    //---------- Draggable ----------
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        draggingPoint.set(e.getPoint());
      }
    });
    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        Point currentPoint = e.getLocationOnScreen();
        Point startPoint =  draggingPoint.get();
        mainFrame.setLocation(currentPoint.x - startPoint.x, currentPoint.y - startPoint.y);
      }
    });
  }

  public void changeStartStopText(boolean isRunning) {
    String text = isRunning ? STOP_TEXT : START_TEXT;
    startStopButton.setText(text);
  }

}
