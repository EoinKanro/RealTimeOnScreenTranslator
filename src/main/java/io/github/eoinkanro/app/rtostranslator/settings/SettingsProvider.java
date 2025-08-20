package io.github.eoinkanro.app.rtostranslator.settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

public class SettingsProvider {

    private static final String SETTINGS_FILE = "settings.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SettingsContext loadSettings() {
        try {
            File file = getSettingsFile();
            if (!file.exists()) {
                return SettingsContext.builder().build();
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            String content = new String(bytes);

            return objectMapper.readValue(content, SettingsContext.class);
        } catch (Exception e) {
            //todo
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return SettingsContext.builder().build();
    }

    public void saveSettings(SettingsContext settings) {
        try {
            String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings);

            Files.writeString(getSettingsFile().toPath(), content);
        } catch (Exception e) {
            //todo
        }
    }

    private File getSettingsFile() {
        return new File(System.getProperty("user.dir") + File.separator + SETTINGS_FILE);
    }

}
