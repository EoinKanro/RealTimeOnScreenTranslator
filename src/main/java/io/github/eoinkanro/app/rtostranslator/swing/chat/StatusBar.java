package io.github.eoinkanro.app.rtostranslator.swing.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {

  private static final Color BACKGROUND = new Color(0, 0, 0, 229);

  private final JLabel statusLabel;

  public StatusBar() {
    setLayout(new BorderLayout());
    setBackground(BACKGROUND);
    setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    statusLabel = new JLabel();
    statusLabel.setForeground(Color.LIGHT_GRAY);
    add(statusLabel, BorderLayout.CENTER);
  }

  public void chageStatus(String status) {
    statusLabel.setText(status);
  }

}
