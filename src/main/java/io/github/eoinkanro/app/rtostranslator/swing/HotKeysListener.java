package io.github.eoinkanro.app.rtostranslator.swing;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.github.eoinkanro.app.rtostranslator.process.message.DoTranslateMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.Message;
import io.github.eoinkanro.app.rtostranslator.process.message.SelectAreaMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.StartStopMessage;
import io.github.eoinkanro.app.rtostranslator.swing.screenselector.ScreenAreaSelectorOverlay;
import io.github.eoinkanro.app.rtostranslator.utils.LogUtils;
import java.util.concurrent.BlockingQueue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HotKeysListener implements NativeKeyListener {

  private final BlockingQueue<Message> output;
  private final ScreenAreaSelectorOverlay screenAreaSelectorOverlay;

  @Override
  public void nativeKeyPressed(NativeKeyEvent event) {
    try {
      if (!isCtrlAlt(event)) {
        return;
      }

      switch (event.getKeyCode()) {
        case NativeKeyEvent.VC_A -> output.add(new SelectAreaMessage());
        case NativeKeyEvent.VC_S -> output.add(new StartStopMessage());
        case NativeKeyEvent.VC_Q -> output.add(new DoTranslateMessage(screenAreaSelectorOverlay.captureScreen()));
      }
    } catch (Exception e) {
      LogUtils.logError(e);
    }
  }

  private boolean isCtrlAlt(NativeKeyEvent event) {
    int modifiers = event.getModifiers();
    return (modifiers & NativeKeyEvent.CTRL_MASK) != 0
        && (modifiers & NativeKeyEvent.ALT_MASK) != 0;
  }

}
