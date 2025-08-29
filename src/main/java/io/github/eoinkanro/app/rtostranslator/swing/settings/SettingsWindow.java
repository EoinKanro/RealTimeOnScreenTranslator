package io.github.eoinkanro.app.rtostranslator.swing.settings;

import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SettingsWindow {

  private boolean isOpened = false;

  public CompletableFuture<SettingsContext> selectSettings(SettingsContext currentSettings) {
    CompletableFuture<SettingsContext> future = new CompletableFuture<>();
    SwingUtilities.invokeLater(() -> processSelection(currentSettings, future));
    return future;
  }

  private void processSelection(SettingsContext currentSettings, CompletableFuture<SettingsContext> resultFuture) {
    if (isOpened) {
      resultFuture.complete(null);
      return;
    }

    isOpened = true;

    SettingsFrame settingsFrame = new SettingsFrame(currentSettings);
    settingsFrame.setVisible(true);
    settingsFrame.requestFocusInWindow();

    settingsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    settingsFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        isOpened = false;
        resultFuture.complete(settingsFrame.getSettingsContext());
        settingsFrame.dispose();
      }
    });
  }

}
