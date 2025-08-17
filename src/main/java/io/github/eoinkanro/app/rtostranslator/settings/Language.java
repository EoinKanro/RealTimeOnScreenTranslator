package io.github.eoinkanro.app.rtostranslator.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum Language {

  AZERBAIJANI("Azerbaijani", "aze"),
  BELARUSIAN("Belarusian", "bel"),
  BOSNIAN("Bosnian", "bos"),
  BULGARIAN("Bulgarian", "bul"),
  CATALAN("Catalan", "cat"),
  CHINESE_SIMPLIFIED("Chinese - Simplified", "chi_sim"),
  CHINESE_TRADITIONAL("Chinese - Traditional", "chi_tra"),
  CROATIAN("Croatian", "hrv"),
  CZECH("Czech", "ces"),
  DANISH("Danish", "dan"),
  DUTCH("Dutch", "nld"),
  ENGLISH("English", "eng"),
  ESTONIAN("Estonian", "est"),
  FINNISH("Finnish", "fin"),
  FRENCH("French", "fra"),
  GEORGIAN("Georgian", "kat"),
  GERMAN("German", "deu"),
  GREEK("Greek", "ell"),
  HUNGARIAN("Hungarian", "hun"),
  ICELANDIC("Icelandic", "isl"),
  ITALIAN("Italian", "ita"),
  IRISH("Irish", "gle"),
  JAPANESE("Japanese", "jpn"),
  KAZAKH("Kazakh", "kaz"),
  KOREAN("Korean", "kor"),
  KYRGYZ("Kyrgyz", "kir"),
  LATIN("Latin", "lat"),
  LATVIAN("Latvian", "lav"),
  MACEDONIAN("Macedonian", "mkd"),
  NORWEGIAN("Norwegian", "nor"),
  POLISH("Polish", "pol"),
  PORTUGUESE("Portuguese", "por"),
  ROMANIAN("Romanian", "rom"),
  RUSSIAN("Russian", "rus"),
  SLOVAK("Slovak", "slk"),
  SLOVENIAN("Slovenian", "slv"),
  SPANISH("Spanish", "spa"),
  SERBIAN("Serbian", "srp"),
  SERBIAN_LATIN("Serbian - Latin", "srp_latn"),
  TURKISH("Turkish", "tur"),
  UKRAINIAN("Ukrainian", "ukr"),
  UZBEK("Uzbek", "uzb"),
  UZBEK_CYRILLIC("Uzbek - Cyrillic", "uzb_cyrl")
  ;

  private final String displayName;
  private final String tesseract;

  @Nullable
  public static Language fromDisplayName(String displayName) {
    for (Language value : Language.values()) {
      if (value.getDisplayName().equals(displayName)) {
        return value;
      }
    }
    return null;
  }

}
