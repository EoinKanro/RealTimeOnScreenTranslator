package io.github.eoinkanro.app.rtostranslator;

import io.github.eoinkanro.app.rtostranslator.ocr.OcrProvider;
import io.github.eoinkanro.app.rtostranslator.ocr.TesseractOcrProvider;
import io.github.eoinkanro.app.rtostranslator.ocr.WindowsOcrProvider;
import io.github.eoinkanro.app.rtostranslator.process.Message;
import io.github.eoinkanro.app.rtostranslator.process.OpenSettingsMessage;
import io.github.eoinkanro.app.rtostranslator.process.SelectAreaMessage;
import io.github.eoinkanro.app.rtostranslator.process.StartStopMessage;
import io.github.eoinkanro.app.rtostranslator.process.UpdateSettingsMessage;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import io.github.eoinkanro.app.rtostranslator.swing.chat.ChatOverlay;
import io.github.eoinkanro.app.rtostranslator.swing.ScreenAreaSelectorOverlay;
import io.github.eoinkanro.app.rtostranslator.swing.SettingsWindow;
import io.github.eoinkanro.app.rtostranslator.translator.OllamaTranslator;
import io.github.eoinkanro.app.rtostranslator.translator.Translator;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

//todo refactor
public class RTOSTranslator {

  private BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
  private AtomicReference<Rectangle> screenArea = new AtomicReference<>();
  private AtomicReference<SettingsContext> settingsContext =  new AtomicReference<>();

  private SettingsWindow settingsWindow = new SettingsWindow();
  private ScreenAreaSelectorOverlay screenAreaSelectorOverlay = new ScreenAreaSelectorOverlay();
  private ChatOverlay chatOverlay = new ChatOverlay(messages);

  private TranslatorThread translator = null;

  private RTOSTranslator() {
    //todo load
    settingsContext.set(SettingsContext.builder().build());
  }

  private void initMessageHandler() {
    Map<Class<?>, Consumer<Object>> handlers = new HashMap<>();

    handlers.put(OpenSettingsMessage.class, message ->
        settingsWindow.selectSettings(settingsContext.get())
            .thenApplyAsync(settings -> messages.add(new UpdateSettingsMessage(settings)))
    );

    handlers.put(SelectAreaMessage.class, message -> selectScreenArea());

    handlers.put(UpdateSettingsMessage.class, message -> {
      //todo save to file
      //todo update when not equals
      settingsContext.set(((UpdateSettingsMessage) message).getData());
      if (isTranslatorRunning()) {
        restartTranslator();
      }
    });

    handlers.put(StartStopMessage.class, message -> {
      if (isTranslatorRunning()) {
        stopTranslator();
      } else {
        restartTranslator();
      }
    });

    //process input messages
    CompletableFuture.runAsync(() -> {
      while (true) {
        try {
          Message message = messages.take();

          Consumer<Object> handler = handlers.get(message.getClass());
          if (handler != null) {
            handler.accept(message);
          }
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      }
    });
  }

  private void selectScreenArea() {
    screenArea.set(screenAreaSelectorOverlay.selectScreenArea().join());
    if (translator != null) {
      translator.setScreenArea(screenArea.get());
    }
  }

  private void restartTranslator() {
    stopTranslator();

    if (screenArea.get() == null) {
      selectScreenArea();
    }

    translator = new TranslatorThread(settingsContext.get(), screenAreaSelectorOverlay, chatOverlay);
    translator.setScreenArea(screenArea.get());
    translator.start();
  }

  private void stopTranslator() {
    if (isTranslatorRunning()) {
      translator.stopGracefully().thenApply(__ -> {
        if(isTranslatorRunning()) {
          translator.interrupt();
        }
        return null;
      });
    }
  }

  private boolean isTranslatorRunning() {
    return translator != null && translator.isRunning;
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  private static class TranslatorThread extends Thread {

    private final SettingsContext settingsContext;
    private final ScreenAreaSelectorOverlay screenAreaSelectorOverlay;
    private final ChatOverlay chatOverlay;

    @Setter
    private Rectangle screenArea;

    private Translator translator;
    private OcrProvider ocrProvider;
    private boolean isRunning = false;

    @Override
    public void run() {
      translator = switch (settingsContext.getTranslatorEngine()) {
        case OLLAMA -> new OllamaTranslator(settingsContext);
        case GOOGLE -> new OllamaTranslator(settingsContext); //todo
      };

      ocrProvider = switch (settingsContext.getOcrEngine()) {
        case WINDOWS_OCR -> {
          try {
            //todo
            yield new WindowsOcrProvider();
          } catch (IOException e) {
            throw new RuntimeException(e);
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
        }
        case TESSERACT_OCR -> new TesseractOcrProvider();
      };

      isRunning = true;
      long period = settingsContext.getUpdatePeriodMs();
      String lastTextToTranslate = null;

      while (isRunning) {
        try {
          long start = System.currentTimeMillis();
          BufferedImage image = screenAreaSelectorOverlay.captureScreen(screenArea);
          chatOverlay.changeStatus("Getting text from screen...");
          String textToTranslate = ocrProvider.getText(image);

          if (!Objects.equals(lastTextToTranslate, textToTranslate)) {
            lastTextToTranslate = textToTranslate;
            chatOverlay.changeStatus("Translating text...");
            chatOverlay.addMessage(translator.translate(textToTranslate));
          }

          long withoutSleeping = System.currentTimeMillis() - start;
          if (withoutSleeping < period) {
            chatOverlay.changeStatus("Waiting period...");
            Thread.sleep(period - withoutSleeping);
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (Exception e) {
          //todo
          System.out.println(e.getMessage());
        }
      }
    }

    public CompletableFuture<Void> stopGracefully() {
      return  CompletableFuture.runAsync(() -> {
        isRunning = false;
        if (translator != null) {
          translator.close();
        }
        if (ocrProvider != null) {
          ocrProvider.close();
        }
      });
    }
  }

  public static void main(String[] args) {
    RTOSTranslator rtosTranslator = new RTOSTranslator();
    rtosTranslator.initMessageHandler();
  }

}
