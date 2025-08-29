package io.github.eoinkanro.app.rtostranslator.process;

import com.github.kwhat.jnativehook.GlobalScreen;
import io.github.eoinkanro.app.rtostranslator.process.message.DoTranslateMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.Message;
import io.github.eoinkanro.app.rtostranslator.process.message.OpenSettingsMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.SelectAreaMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.StartStopMessage;
import io.github.eoinkanro.app.rtostranslator.process.message.UpdateSettingsMessage;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsProvider;
import io.github.eoinkanro.app.rtostranslator.swing.chat.ChatOverlay;
import io.github.eoinkanro.app.rtostranslator.swing.HotKeysListener;
import io.github.eoinkanro.app.rtostranslator.swing.screenselector.ScreenAreaSelectorOverlay;
import io.github.eoinkanro.app.rtostranslator.swing.settings.SettingsWindow;

import io.github.eoinkanro.app.rtostranslator.utils.LogUtils;
import java.awt.AWTException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.JOptionPane;

public class ApplicationProcessor extends Thread {

    private static final String ERROR_STATUS = "Error";
    private static final String STOPPED_STATUS = "Stopped";
    private static final String ONE_TRANSLATE_ERROR = "You can't translate manually when translator is running. Stop it and try again.";

    private final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private final AtomicReference<SettingsContext> settingsContext = new AtomicReference<>();

    private final SettingsWindow settingsWindow = new SettingsWindow();
    private final ChatOverlay chatOverlay = new ChatOverlay(messages);
    private final ScreenAreaSelectorOverlay screenAreaSelectorOverlay;

    private final SettingsProvider settingsProvider = new SettingsProvider();

    private final Map<Class<?>, Consumer<Object>> handlers = new HashMap<>();

    private TranslationProcessor translationProcessor;

    public ApplicationProcessor() throws AWTException {
        screenAreaSelectorOverlay = new ScreenAreaSelectorOverlay(chatOverlay);

        settingsContext.set(settingsProvider.loadSettings());

        handlers.put(OpenSettingsMessage.class, message -> openSettings());
        handlers.put(UpdateSettingsMessage.class, message -> updateSettings(((UpdateSettingsMessage) message).getData()));
        handlers.put(SelectAreaMessage.class, message -> selectScreenArea());
        handlers.put(DoTranslateMessage.class, message -> doOneTranslate((DoTranslateMessage) message));

        handlers.put(StartStopMessage.class, message -> {
            if (isTranslatorRunning()) {
                stopTranslator();
            } else {
                restartTranslator();
            }
        });

        enableHotKeys();
    }

    private void enableHotKeys() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new HotKeysListener(messages, screenAreaSelectorOverlay));
        } catch (Exception e) {
            LogUtils.logError(e);
        }
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
        } else if (translationProcessor != null) {
            recreateTranslator();
        }
    }

    private void selectScreenArea() {
        screenAreaSelectorOverlay.selectScreenArea();
    }

    private void recreateTranslator() {
        stopTranslator();

        try {
            createTranslationProcessor();
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private void restartTranslator() {
        recreateTranslator();

        translationProcessor.start();
        chatOverlay.updateRunningStatus(true);
    }

    private void createTranslationProcessor() throws TranslationCreationException {
        try {
            if (screenAreaSelectorOverlay.getScreenArea() == null) {
                selectScreenArea();
            }

            translationProcessor = new TranslationProcessor(settingsContext.get(), screenAreaSelectorOverlay, chatOverlay);
        } catch (Exception e) {
            chatOverlay.addMessage(e.getMessage());
            chatOverlay.changeStatus(ERROR_STATUS);
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
            chatOverlay.changeStatus(STOPPED_STATUS);
        }
    }

    private void doOneTranslate(DoTranslateMessage message) {
        if (isTranslatorRunning()) {
            JOptionPane.showMessageDialog(
                null,
                ONE_TRANSLATE_ERROR,
                ERROR_STATUS,
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

        translationProcessor.doForceTranslate(message.getData());
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
