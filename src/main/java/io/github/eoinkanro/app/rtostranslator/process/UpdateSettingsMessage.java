package io.github.eoinkanro.app.rtostranslator.process;

import io.github.eoinkanro.app.rtostranslator.settings.SettingsContext;

public class UpdateSettingsMessage extends MessageWithData<SettingsContext> {

  public UpdateSettingsMessage(SettingsContext data) {
    super(data);
  }

}
