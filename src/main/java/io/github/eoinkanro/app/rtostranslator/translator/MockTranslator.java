package io.github.eoinkanro.app.rtostranslator.translator;

public class MockTranslator implements Translator {

    private int messageCounter = 0;

    @Override
    public String translate(String text) {
        return "Message " + messageCounter++;
    }

}
