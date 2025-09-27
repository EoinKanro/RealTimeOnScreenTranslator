package io.github.eoinkanro.app.rtostranslator.swing.chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;

class MenuButton extends JButton {

  private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 14);

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
    setFont(FONT);
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
