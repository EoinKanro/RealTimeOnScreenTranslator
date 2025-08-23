package io.github.eoinkanro.app.rtostranslator.swing.settings;

import io.github.eoinkanro.app.rtostranslator.settings.Language;
import io.github.eoinkanro.app.rtostranslator.settings.OcrEngine;
import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;
import io.github.eoinkanro.app.rtostranslator.settings.TranslatorEngine;

import io.github.eoinkanro.app.rtostranslator.swing.SwingUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class SettingsFrame extends JFrame {

    private static final String GENERAL = "General";
    private static final String OCR = "OCR";
    private static final String TRANSLATOR = "Translator";

    private static final String UPDATE_PERIOD_TOOLTIP = "How often to check screen";
    private static final String OCR_TOOLTIP = "OCR recognizes text on images";

    private static final String WINDOWS_DESCRIPTION = "Windows OCR works only on Windows OS."
            + "\nNo extra settings required.";
    private static final String TESSERACT_DESCRIPTION = "You can install it from: https://github.com/tesseract-ocr/tesseract"
            + "\n\nAlso download model for source language and put it inside \"tesseract\" folder near jar."
            + "\nAverage models: https://github.com/tesseract-ocr/tessdata"
            + "\nSlowest models: https://github.com/tesseract-ocr/tessdata_best"
            + "\n\nAdditional Information: https://tesseract-ocr.github.io/tessdoc/Data-Files";
    private static final String HOTKEYS_DESCRIPTION = "CTRL+ALT+A - select area of screen to translate"
        + "\nCTRL+ALT+S - start\\stop auto translator"
        + "\nCTRL+ALT+Q - do one manual translation";

    private JComboBox<String> sourceLanguages;
    private JComboBox<String> targetLanguages;
    private SpinnerNumberModel updatePeriodModel;

    private JComboBox<String> ocrEngines;

    private JComboBox<String> translator;
    private JTextField modelField;
    private JTextField addressField;
    private SpinnerNumberModel portModel;
    private JTextArea promptArea;

    public SettingsFrame(SettingsContext currentSettings) {
        SettingsContext settingsContext = currentSettings == null ?
                SettingsContext.builder().build()
                : currentSettings;

        setTitle("Settings");
        setSize(600, 400);
        setLocationRelativeTo(null);

        //-------- Navigation ---------
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGeneral = new JButton(GENERAL);
        JButton btnOCR = new JButton(OCR);
        JButton btnTranslator = new JButton(TRANSLATOR);

        navPanel.add(btnGeneral);
        navPanel.add(btnOCR);
        navPanel.add(btnTranslator);

        //----------- Content -----------
        CardLayout cardLayout = new CardLayout();
        JPanel mainContent = new JPanel(cardLayout);

        mainContent.add(createGeneralPanel(), GENERAL);
        mainContent.add(createOCRPanel(), OCR);
        mainContent.add(createTranslatorPanel(), TRANSLATOR);

        btnGeneral.addActionListener((ActionEvent e) -> cardLayout.show(mainContent, GENERAL));
        btnOCR.addActionListener((ActionEvent e) -> cardLayout.show(mainContent, OCR));
        btnTranslator.addActionListener((ActionEvent e) -> cardLayout.show(mainContent, TRANSLATOR));

        //----------- Save ----------
        JButton btnRestore = new JButton("Restore default");
        btnRestore.addActionListener((ActionEvent e) -> setDefaultValuesToFields());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnRestore);

        //----------- Layout ------------
        setLayout(new BorderLayout());
        add(navPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        SwingUtils.setIcon(this);

        setSettingsToFields(settingsContext);
        setVisible(true);
    }

    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        FontMetrics fontMetrics = panel.getFontMetrics(panel.getFont());

        //------- Source Language -----
        JLabel sourceLanguageLabel = new JLabel("Source Language:");
        sourceLanguageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sourceLanguageLabel);

        sourceLanguages = new JComboBox<>();
        sourceLanguages.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourceLanguages.setMaximumSize(new Dimension(Integer.MAX_VALUE, fontMetrics.getHeight()));
        addLanguages(sourceLanguages);
        panel.add(sourceLanguages);

        //-------  Target Language -------
        JLabel tagetLanguageLabel = new JLabel("Target Language:");
        tagetLanguageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tagetLanguageLabel);

        targetLanguages = new JComboBox<>();
        targetLanguages.setAlignmentX(Component.LEFT_ALIGNMENT);
        targetLanguages.setMaximumSize(new Dimension(Integer.MAX_VALUE, fontMetrics.getHeight()));
        addLanguages(targetLanguages);
        panel.add(targetLanguages);

        //-------- Update period --------
        JLabel updatePeriodLabel = new JLabel("Update period (ms):");
        updatePeriodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatePeriodLabel.setToolTipText(UPDATE_PERIOD_TOOLTIP);
        panel.add(updatePeriodLabel);

        updatePeriodModel = new SpinnerNumberModel(500, 1, null, 1);
        JSpinner updatePeriod = new JSpinner(updatePeriodModel);
        updatePeriod.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatePeriod.setToolTipText(UPDATE_PERIOD_TOOLTIP);
        updatePeriod.setMaximumSize(new Dimension(Integer.MAX_VALUE, fontMetrics.getHeight()));
        panel.add(updatePeriod);

        //-------- Hot keys ---------
        panel.add(Box.createVerticalStrut(20));

        JTextArea hotKeys = new JTextArea(HOTKEYS_DESCRIPTION);
        hotKeys.setEditable(false);
        hotKeys.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane hotKeysScroll = new JScrollPane(hotKeys);
        hotKeysScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hotKeysScroll);

        return panel;
    }

    private void addLanguages(JComboBox<String> dropdown) {
        for (Language value : Language.values()) {
            dropdown.addItem(value.getDisplayName());
        }
    }

    private JPanel createOCRPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //------- Dropdown --------
        JLabel dropdownHeader = new JLabel("OCR Engine:");
        dropdownHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        dropdownHeader.setToolTipText(OCR_TOOLTIP);
        panel.add(dropdownHeader);

        ocrEngines = new JComboBox<>();
        for (OcrEngine value : OcrEngine.values()) {
            ocrEngines.addItem(value.getDisplayName());
        }
        ocrEngines.setAlignmentX(Component.LEFT_ALIGNMENT);
        ocrEngines.setToolTipText(OCR_TOOLTIP);

        Dimension preferred = ocrEngines.getPreferredSize();
        ocrEngines.setMaximumSize(new Dimension(preferred.width, preferred.height));

        panel.add(ocrEngines);

        panel.add(Box.createVerticalStrut(20));

        //-------- Description ------
        JTextArea engineDescription = new JTextArea();
        engineDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
        engineDescription.setEditable(false);
        JScrollPane engineDescriptionPane = new JScrollPane(engineDescription);
        engineDescriptionPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(engineDescriptionPane);

        engineDescription.setText(WINDOWS_DESCRIPTION);

        ocrEngines.addActionListener(e -> {
            String selected = (String) ocrEngines.getSelectedItem();
            OcrEngine selectedEngine = OcrEngine.fromDisplayName(selected);

            if (OcrEngine.WINDOWS_OCR == selectedEngine) {
                engineDescription.setText(WINDOWS_DESCRIPTION);
            } else if (OcrEngine.TESSERACT_OCR == selectedEngine) {
                engineDescription.setText(TESSERACT_DESCRIPTION);
            } else {
                engineDescription.setText(null);
            }
        });

        return panel;
    }

    private JPanel createTranslatorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //----- Translator -------
        JLabel translatorLabel = new JLabel("Translator:");
        translatorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(translatorLabel);

        translator = new JComboBox<>();
        translator.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (TranslatorEngine value : TranslatorEngine.values()) {
            translator.addItem(value.getDisplayName());
        }
        Dimension preferred = translator.getPreferredSize();
        translator.setMaximumSize(new Dimension(preferred.width, preferred.height));
        panel.add(translator);

        //-------- Additional settings -----------
        JPanel ollamaPanel = createOllamaPanel();
        panel.add(ollamaPanel);

        translator.addActionListener(e -> {
            String selected = (String) translator.getSelectedItem();
            TranslatorEngine translatorEngine = TranslatorEngine.fromDisplayName(selected);
            ollamaPanel.setVisible(TranslatorEngine.OLLAMA == translatorEngine);
        });

        return panel;
    }

    private JPanel createOllamaPanel() {
        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        FontMetrics fontMetrics = panel.getFontMetrics(panel.getFont());

        //------ Model ------
        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(modelLabel);

        modelField = new JTextField();
        modelField.setAlignmentX(Component.LEFT_ALIGNMENT);
        modelField.setText("qwen3:4b");
        modelField.setMaximumSize(new Dimension(Integer.MAX_VALUE, fontMetrics.getHeight()));
        panel.add(modelField);

        //------ Address -------
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(addressLabel);

        addressField = new JTextField();
        addressField.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressField.setText("localhost");
        addressField.setMaximumSize(new Dimension(Integer.MAX_VALUE, fontMetrics.getHeight()));
        panel.add(addressField);

        //------- Port -------
        JLabel portLabel = new JLabel("Port:");
        portLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(portLabel);

        portModel = new SpinnerNumberModel(11434, 1, 65535, 1);
        JSpinner portSpinner = new JSpinner(portModel);
        portSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        portSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, fontMetrics.getHeight()));
        panel.add(portSpinner);

        //------ Prompt ------
        JLabel promptLabel = new JLabel("Prompt:");
        promptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(promptLabel);

        promptArea = new JTextArea();
        promptArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(promptArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollPane);

        return panel;
    }

    private void setDefaultValuesToFields() {
        setSettingsToFields(SettingsContext.builder().build());
    }

    private void setSettingsToFields(SettingsContext settingsContext) {
        sourceLanguages.setSelectedIndex(getDropdownIndex(settingsContext.getSourceLanguage().getDisplayName(),
                sourceLanguages.getModel()));

        targetLanguages.setSelectedIndex(getDropdownIndex(settingsContext.getTargetLanguage().getDisplayName(),
                targetLanguages.getModel()));

        updatePeriodModel.setValue(settingsContext.getUpdatePeriodMs());

        ocrEngines.setSelectedIndex(getDropdownIndex(settingsContext.getOcrEngine().getDisplayName(),
                ocrEngines.getModel()));

        translator.setSelectedIndex(getDropdownIndex(settingsContext.getTranslatorEngine().getDisplayName(),
                translator.getModel()));

        modelField.setText(settingsContext.getTranslatorModel());
        addressField.setText(settingsContext.getTranslatorAddress());
        portModel.setValue(settingsContext.getTranslatorPort());
        promptArea.setText(settingsContext.getTranslatorPrompt());
    }

    private int getDropdownIndex(String item, ComboBoxModel<String> dropdownModel) {
        for (int i = 0; i < dropdownModel.getSize(); i++) {
            if (dropdownModel.getElementAt(i).equals(item)) {
                return i;
            }
        }
        return 0;
    }

    public SettingsContext getSettingsContext() {
        return SettingsContext.builder()
                .sourceLanguage(Language.fromDisplayName((String) sourceLanguages.getSelectedItem()))
                .targetLanguage(Language.fromDisplayName((String) targetLanguages.getSelectedItem()))
                .updatePeriodMs(updatePeriodModel.getNumber().longValue())
                .ocrEngine(OcrEngine.fromDisplayName((String) ocrEngines.getSelectedItem()))
                .translatorEngine(TranslatorEngine.fromDisplayName((String) translator.getSelectedItem()))
                .translatorModel(modelField.getText())
                .translatorAddress(addressField.getText())
                .translatorPort(portModel.getNumber().intValue())
                .translatorPrompt(promptArea.getText())
                .build();
    }
}
