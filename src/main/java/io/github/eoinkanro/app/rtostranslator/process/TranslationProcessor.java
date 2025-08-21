package io.github.eoinkanro.app.rtostranslator.process;

import io.github.eoinkanro.app.rtostranslator.ocr.OcrProvider;
import io.github.eoinkanro.app.rtostranslator.ocr.TesseractOcrProvider;
import io.github.eoinkanro.app.rtostranslator.ocr.WindowsOcrProvider;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import io.github.eoinkanro.app.rtostranslator.swing.chat.ChatOverlay;
import io.github.eoinkanro.app.rtostranslator.swing.screenselector.ScreenAreaSelectorOverlay;
import io.github.eoinkanro.app.rtostranslator.translator.MockTranslator;
import io.github.eoinkanro.app.rtostranslator.translator.OllamaTranslator;
import io.github.eoinkanro.app.rtostranslator.translator.Translator;
import io.github.eoinkanro.app.rtostranslator.utils.LogUtils;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.util.Objects;

public class TranslationProcessor extends Thread implements Closeable {

    private final SettingsContext settingsContext;
    private final ScreenAreaSelectorOverlay screenAreaSelectorOverlay;
    private final ChatOverlay chatOverlay;

    private final Translator translator;
    private final OcrProvider ocrProvider;

    @Setter
    private Rectangle screenArea;

    private boolean isRunning = false;
    private String lastCapturedText;

    public TranslationProcessor(SettingsContext settingsContext, ScreenAreaSelectorOverlay screenAreaSelectorOverlay, ChatOverlay chatOverlay) throws Exception {
        this.settingsContext = settingsContext;
        this.screenAreaSelectorOverlay = screenAreaSelectorOverlay;
        this.chatOverlay = chatOverlay;

        translator = switch (settingsContext.getTranslatorEngine()) {
            case OLLAMA -> new OllamaTranslator(settingsContext);
            case GOOGLE -> new MockTranslator(); //todo
        };

        ocrProvider = switch (settingsContext.getOcrEngine()) {
            case WINDOWS_OCR -> new WindowsOcrProvider();
            case TESSERACT_OCR -> new TesseractOcrProvider(settingsContext);
        };
    }

    @Override
    public void run() {
        if (translator == null || ocrProvider == null) {
            return;
        }

        isRunning = true;
        long period = settingsContext.getUpdatePeriodMs();
        while (isRunning) {
            try {
                long start = System.currentTimeMillis();

                doTranslate();

                long withoutSleeping = System.currentTimeMillis() - start;
                if (withoutSleeping < period) {
                    chatOverlay.changeStatus("Sleeping...");
                    Thread.sleep(period - withoutSleeping);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LogUtils.logError(e);
            }
        }
    }

    public void doTranslate() {
        try {
            BufferedImage image = screenAreaSelectorOverlay.captureScreen(screenArea);
            chatOverlay.changeStatus("Getting text from screen...");
            String textToTranslate = ocrProvider.getText(image);

            if (textToTranslate != null) {
                textToTranslate = textToTranslate.trim();
            }

            if (!Objects.equals(lastCapturedText, textToTranslate)) {
                lastCapturedText = textToTranslate;
                chatOverlay.changeStatus("Translating text...");
                chatOverlay.addMessage(translator.translate(textToTranslate));
            }

            chatOverlay.changeStatus("Done");
        } catch (Exception e) {
            chatOverlay.changeStatus("Error");
            LogUtils.logError(e);
        }
    }

    @Override
    public void close() {
        isRunning = false;
        closeComponent(translator);
        closeComponent(ocrProvider);
    }

    private void closeComponent(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LogUtils.logError(e);
            }
        }
    }

}
