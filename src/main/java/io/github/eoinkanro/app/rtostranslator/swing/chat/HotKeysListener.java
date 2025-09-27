package io.github.eoinkanro.app.rtostranslator.swing.chat;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.github.eoinkanro.app.rtostranslator.process.message.DoTranslateMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.Message;
import io.github.eoinkanro.app.rtostranslator.process.message.SelectAreaMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.StartStopMessage;
import java.util.concurrent.BlockingQueue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HotKeysListener implements NativeKeyListener {

  private final BlockingQueue<Message> output;

  @Override
  public void nativeKeyPressed(NativeKeyEvent event) {
    if (!isCtrlAlt(event)) {
      return;
    }

    switch (event.getKeyCode()) {
      case NativeKeyEvent.VC_A -> output.add(new SelectAreaMessage());
      case NativeKeyEvent.VC_S -> output.add(new StartStopMessage());
      case NativeKeyEvent.VC_Q -> output.add(new DoTranslateMessage());
    }
  }

  private boolean isCtrlAlt(NativeKeyEvent event) {
    int modifiers = event.getModifiers();
    return (modifiers & NativeKeyEvent.CTRL_MASK) != 0
        && (modifiers & NativeKeyEvent.ALT_MASK) != 0;
  }

}
