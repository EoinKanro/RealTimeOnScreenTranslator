package io.github.eoinkanro.app.rtostranslator.process;

import io.github.eoinkanro.app.rtostranslator.process.message.DoTranslateMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.Message;
import io.github.eoinkanro.app.rtostranslator.process.message.OpenSettingsMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.SelectAreaMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.StartStopMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.UpdateSettingsMessage;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsProvider;
import io.github.eoinkanro.app.rtostranslator.swing.chat.ChatOverlay;
import io.github.eoinkanro.app.rtostranslator.swing.screenselector.ScreenAreaSelectorOverlay;
import io.github.eoinkanro.app.rtostranslator.swing.settings.SettingsWindow;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.JOptionPane;

public class ApplicationProcessor extends Thread {

    private final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private final AtomicReference<Rectangle> screenArea = new AtomicReference<>();
    private final AtomicReference<SettingsContext> settingsContext = new AtomicReference<>();

    private final SettingsWindow settingsWindow = new SettingsWindow();
    private final ScreenAreaSelectorOverlay screenAreaSelectorOverlay = new ScreenAreaSelectorOverlay();
    private final ChatOverlay chatOverlay = new ChatOverlay(messages);

    private final SettingsProvider settingsProvider = new SettingsProvider();

    private final Map<Class<?>, Consumer<Object>> handlers = new HashMap<>();

    private TranslationProcessor translationProcessor;

    public ApplicationProcessor() {
        settingsContext.set(settingsProvider.loadSettings());

        handlers.put(OpenSettingsMessage.class, message -> openSettings());
        handlers.put(UpdateSettingsMessage.class, message -> updateSettings(((UpdateSettingsMessage) message).getData()));
        handlers.put(SelectAreaMessage.class, message -> selectScreenArea());
        handlers.put(DoTranslateMessage.class, message -> doOneTranslate());

        handlers.put(StartStopMessage.class, message -> {
            if (isTranslatorRunning()) {
                stopTranslator();
            } else {
                restartTranslator();
            }
        });
    }

    private void openSettings() {
        settingsWindow.selectSettings(settingsContext.get())
                .thenApplyAsync(settings ->
                        messages.add(new UpdateSettingsMessage(settings)));
    }

    private void updateSettings(SettingsContext newSettings) {
        if (Objects.equals(settingsContext.get(), newSettings)) {
            return;
        }

        settingsProvider.saveSettings(newSettings);
        settingsContext.set(newSettings);

        if (isTranslatorRunning()) {
            restartTranslator();
        }
    }

    private void selectScreenArea() {
        chatOverlay.setVisible(false);
        screenArea.set(screenAreaSelectorOverlay.selectScreenArea().join());
        if (translationProcessor != null) {
            translationProcessor.setScreenArea(screenArea.get());
        }
        chatOverlay.setVisible(true);
    }

    private void restartTranslator() {
        stopTranslator();

        try {
            createTranslationProcessor();
        } catch (Exception e) {
            return;
        }

        translationProcessor.start();
        chatOverlay.updateRunningStatus(true);
    }

    private void createTranslationProcessor() throws TranslationCreationException {
        try {
            if (screenArea.get() == null) {
                selectScreenArea();
            }

            translationProcessor = new TranslationProcessor(settingsContext.get(), screenAreaSelectorOverlay, chatOverlay);
            translationProcessor.setScreenArea(screenArea.get());
        } catch (Exception e) {
            chatOverlay.addMessage(e.getMessage());
            chatOverlay.changeStatus("Error");
            throw new TranslationCreationException(e);
        }
    }

    private void stopTranslator() {
        if (isTranslatorRunning()) {
            translationProcessor.close();

            if (isTranslatorRunning()) {
                translationProcessor.interrupt();
            }
            translationProcessor = null;

            chatOverlay.updateRunningStatus(false);
            chatOverlay.changeStatus("Stopped");
        }
    }

    private void doOneTranslate() {
        if (isTranslatorRunning()) {
            JOptionPane.showMessageDialog(
                null,
                "You can't translate manually when translator is running. Stop it and try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (translationProcessor == null) {
            try {
                createTranslationProcessor();
            } catch (TranslationCreationException e) {
                return;
            }
        }

        translationProcessor.doTranslate();
    }

    private boolean isTranslatorRunning() {
        return translationProcessor != null && translationProcessor.isAlive();
    }

    @Override
    public void run() {
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
    }

}
