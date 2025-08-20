package io.github.eoinkanro.app.rtostranslator.swing.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Deque;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import lombok.Getter;

public class ChatContent extends JPanel implements Scrollable {

  private static final int MAX_MESSAGES_COUNT = 50;

  private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 16);

  private static final Color BACKGROUND = new Color(0, 0, 0, 190);
  private static final Color SCROLLBAR_BACKGROUND = new Color(0, 0, 0, 50);
  private static final Color SCROLLBAR_THUMB = new Color(141, 141, 141, 100);

  private final Deque<JPanel> chatHistory;

  @Getter
  private final JScrollPane chatScrollPane;

  public ChatContent() {
    this.chatHistory = new LinkedList<>();

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setOpaque(false);
    setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

    chatScrollPane = new JScrollPane(this) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(BACKGROUND);
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
    chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
  }

  public void addMessage(String text) {
    JPanel messagePanel = new JPanel();
    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
    messagePanel.setOpaque(false);

    JTextArea textArea = new JTextArea();
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setEditable(false);
    textArea.setOpaque(false);
    textArea.setText(text);
    textArea.setFont(FONT);
    textArea.setForeground(Color.WHITE);

    messagePanel.add(textArea);
    messagePanel.add(Box.createVerticalStrut(10));

    add(messagePanel);
    chatHistory.addLast(messagePanel);

    if (chatHistory.size() > MAX_MESSAGES_COUNT) {
      JPanel oldMessage = chatHistory.removeFirst();
      remove(oldMessage);
    }

    revalidate();
    repaint();

    JScrollBar scrollBar = chatScrollPane.getVerticalScrollBar();
    scrollBar.setValue(scrollBar.getMaximum());
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
    return FONT.getSize();
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
    return Math.max(FONT.getSize(), r.height - FONT.getSize());
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return true;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

}
