package io.github.eoinkanro.app.rtostranslator.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import lombok.RequiredArgsConstructor;

public class ChatOverlay extends JFrame {

  private static final int MAX_MESSAGES_COUNT = 50;

  private static final Font CHAT_FONT = new Font(Font.DIALOG, Font.BOLD, 16);
  private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
  private static final Color NAVIGATION_BACKGROUND = new Color(0, 0, 0, 229);
  private static final Color CONTENT_BACKGROUND = new Color(0, 0, 0, 190);
  private static final Color SCROLLBAR_BACKGROUND = new Color(0, 0, 0, 50);
  private static final Color SCROLLBAR_THUMB = new Color(141, 141, 141, 100);

  private final AtomicReference<Point> draggingPoint = new AtomicReference<>();
  private final AtomicReference<Point> resizingPoint = new AtomicReference<>();
  private final AtomicReference<Rectangle> resizingBounds = new AtomicReference<>();

  private JPanel chatPanel;
  private JScrollPane chatScrollPane;
  private Deque<JPanel> chatMessages;

  public ChatOverlay() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setUndecorated(true);
    setBackground(TRANSPARENT);
    setSize(300, 300);
    setAlwaysOnTop(true);
    setLayout(new BorderLayout());

    createNavigationBar();
    createChatContent();
    enableResizing();

    setVisible(true);
    requestFocusInWindow();
  }

  private void createNavigationBar() {
    JPanel navigation = new JPanel();
    navigation.setLayout(new BorderLayout());
    navigation.setBackground(NAVIGATION_BACKGROUND);

    MenuButton closeButton = new MenuButton("X");
    navigation.add(closeButton, BorderLayout.EAST);
    closeButton.addActionListener(e -> System.exit(0));

    JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    actionButtons.setOpaque(false);
    //TODO logic
    MenuButton startStopButton = new MenuButton("Start");
    actionButtons.add(startStopButton);

    //todo logic
    MenuButton settingsButton = new MenuButton("Settings");
    actionButtons.add(settingsButton);

    navigation.add(actionButtons, BorderLayout.WEST);

    //-------- Draggable --------
    navigation.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        draggingPoint.set(e.getPoint());
      }
    });
    navigation.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        Point currentPoint = e.getLocationOnScreen();
        Point startPoint =  draggingPoint.get();
        setLocation(currentPoint.x - startPoint.x, currentPoint.y - startPoint.y);
      }
    });

    add(navigation, BorderLayout.NORTH);
  }

  private void createChatContent() {
    chatMessages = new LinkedList<>();

    chatPanel = new JPanel();
    chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
    chatPanel.setOpaque(false);
    chatPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

    chatScrollPane = new JScrollPane(chatPanel) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(CONTENT_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
      }
    };

    chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    chatScrollPane.setOpaque(false);
    chatScrollPane.getViewport().setOpaque(false);
    chatScrollPane.setBorder(null);

    chatScrollPane.getVerticalScrollBar().setOpaque(false);
    chatScrollPane.getHorizontalScrollBar().setOpaque(false);
    chatScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
      @Override
      protected void configureScrollBarColors() {
        this.thumbColor = SCROLLBAR_THUMB;
        this.trackColor = SCROLLBAR_BACKGROUND;
      }

      @Override
      protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
      }

      @Override
      protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
      }

      private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
      }
    });

    add(chatScrollPane, BorderLayout.CENTER);
  }

  private void enableResizing() {
    chatPanel.addMouseListener(new ResizeMouseAdapter(this,
        resizingPoint,
        resizingBounds));

    chatPanel.addMouseMotionListener(new ResizeMouseMotionAdapter(
        this,
        resizingPoint,
        resizingBounds));
  }

  public void addMessage(String text) {
    JPanel messagePanel = new JPanel();
    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
    messagePanel.setOpaque(false);

    JLabel messageLabel = new JLabel(text);
    messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    messageLabel.setFont(CHAT_FONT);
    messageLabel.setForeground(Color.WHITE);

    messagePanel.add(messageLabel);
    messagePanel.add(Box.createVerticalStrut(10));

    chatPanel.add(messagePanel);
    chatMessages.addLast(messagePanel);

    if (chatMessages.size() > MAX_MESSAGES_COUNT) {
      JPanel oldMessage = chatMessages.removeFirst();
      chatPanel.remove(oldMessage);
    }

    chatPanel.revalidate();
    chatPanel.repaint();

    JScrollBar scrollBar = chatScrollPane.getVerticalScrollBar();
    scrollBar.setValue(scrollBar.getMaximum());
  }


  @RequiredArgsConstructor
  private static class ResizeMouseAdapter extends MouseAdapter {

    private final JFrame overlayFrame;
    private final AtomicReference<Point> resizingPoint;
    private final AtomicReference<Rectangle> resizingBounds;

    @Override
    public void mousePressed(MouseEvent e) {
      resizingPoint.set(e.getLocationOnScreen());
      resizingBounds.set(new Rectangle(overlayFrame.getBounds()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      resizingPoint.set(null);
      resizingBounds.set(null);
    }

  }

  @RequiredArgsConstructor
  private static class ResizeMouseMotionAdapter extends MouseMotionAdapter {

    private static final int BORDER_SIZE = 7;

    private final JFrame overlayFrame;
    private final AtomicReference<Point> resizingPoint;
    private final AtomicReference<Rectangle> resizingBounds;

    @Override
    public void mouseMoved(MouseEvent e) {
      Point frameLocation = overlayFrame.getLocationOnScreen();
      Point mouseLocation = e.getLocationOnScreen();
      int x = mouseLocation.x - frameLocation.x;
      int y = mouseLocation.y - frameLocation.y;
      int width = overlayFrame.getWidth();
      int height = overlayFrame.getHeight();

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

      overlayFrame.setCursor(Cursor.getPredefinedCursor(cursor));
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

      switch (overlayFrame.getCursor().getType()) {
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

      if (bounds.width >= 200 && bounds.height >= 150) {
        overlayFrame.setBounds(bounds);
      }
    }
  }

  private static class MenuButton extends JButton {

    private static final Font MENU_FONT = new Font(Font.DIALOG, Font.BOLD, 14);

    private static final Color MENU_BUTTON_BACKGROUND = new Color(51, 51, 51, 150);
    private static final Color MENU_BUTTON_BACKGROUND_HOVER = new Color(40, 40, 40, 150);
    private static final Color MENU_BUTTON_BACKGROUND_PRESSED = new Color(65, 65, 65, 205);

    public MenuButton(String text) {
      super(text);
      setContentAreaFilled(false);
      setFocusPainted(false);
      setBorderPainted(false);
      setOpaque(false);
      setForeground(Color.WHITE);
      setFont(MENU_FONT);
    }

    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D safeCopy = (Graphics2D) g.create();

      if (getModel().isPressed()) {
        safeCopy.setColor(MENU_BUTTON_BACKGROUND_PRESSED);
      } else if (getModel().isRollover()) {
        safeCopy.setColor(MENU_BUTTON_BACKGROUND_HOVER);
      } else {
        safeCopy.setColor(MENU_BUTTON_BACKGROUND);
      }

      safeCopy.fillRect(0, 0, getWidth(), getHeight());
      safeCopy.dispose();

      super.paintComponent(g);
    }
  }

  public static void main(String[] args) {
    ChatOverlay chatFrame = new ChatOverlay();

    for (int i = 0; i < 50; i++) {
      chatFrame.addMessage("Message " + i);
    }
    chatFrame.addMessage("NEW MESSAGE");
    chatFrame.addMessage("NEW MESSAGE2");
  }

}
