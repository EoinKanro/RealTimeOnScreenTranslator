package io.github.eoinkanro.app.rtostranslator.swing.chat;

import io.github.eoinkanro.app.rtostranslator.process.message.Message;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChatOverlay extends JFrame {

  private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

  private final AtomicReference<Point> resizingPoint = new AtomicReference<>();
  private final AtomicReference<Rectangle> resizingBounds = new AtomicReference<>();

  private final NavigationBar navigationBar;
  private final ChatContent chatContent;
  private final StatusBar statusBar;

  public ChatOverlay(BlockingQueue<Message> output) {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setUndecorated(true);
    setBackground(TRANSPARENT);
    setSize(300, 300);
    setAlwaysOnTop(true);
    setLayout(new BorderLayout());

    this.navigationBar = new NavigationBar(this, output);
    add(navigationBar, BorderLayout.NORTH);

    this.chatContent = new ChatContent();
    add(chatContent.getChatScrollPane(), BorderLayout.CENTER);

    this.statusBar = new StatusBar();
    add(statusBar, BorderLayout.SOUTH);
    statusBar.chageStatus("Stopped");

    enableResizing(chatContent);
    enableResizing(statusBar);

    setVisible(true);
    requestFocusInWindow();
  }

  private void enableResizing(JPanel panel) {
    panel.addMouseListener(new BottomResizeMouseAdapter(this,
        resizingPoint,
        resizingBounds));

    panel.addMouseMotionListener(new BottomResizeMouseMotionAdapter(
        this,
        resizingPoint,
        resizingBounds));
  }

  public void changeStatus(String text) {
    statusBar.chageStatus(text);
  }

  public void addMessage(String text) {
    chatContent.addMessage(text);
  }

  public void updateRunningStatus(boolean isRunning) {
      navigationBar.changeStartStopText(isRunning);
  }

  public static void main(String[] args) {
    ChatOverlay chatFrame = new ChatOverlay(new LinkedBlockingQueue<>());

    for (int i = 0; i < 50; i++) {
      chatFrame.addMessage("Message " + i);
    }
    chatFrame.addMessage("NEW MESSAGE");
    chatFrame.addMessage("NEW MESSAGE2");
    chatFrame.addMessage("TEST a;skda;skd;aks;dka ;skd;laks kdoirrgiu hrth woierjoiwe ksjfdbfhj gsebrtb abwejhjfrb awieraiu wheoharwoeih aiwbekrjabw eraiuwehria hwuehraweb kjawekjrbawkej");
  }

}
