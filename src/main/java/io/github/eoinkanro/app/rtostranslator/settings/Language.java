package io.github.eoinkanro.app.rtostranslator.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum Language {

  AZERBAIJANI("Azerbaijani", "aze", "az"),
  BELARUSIAN("Belarusian", "bel", "be"),
  BOSNIAN("Bosnian", "bos", "bs"),
  BULGARIAN("Bulgarian", "bul", "bg"),
  CATALAN("Catalan", "cat", "ca"),
  CHINESE_SIMPLIFIED("Chinese - Simplified", "chi_sim", "zh-CN"),
  CHINESE_TRADITIONAL("Chinese - Traditional", "chi_tra", "zh-TW"),
  CROATIAN("Croatian", "hrv", "hr"),
  CZECH("Czech", "ces", "cs"),
  DANISH("Danish", "dan", "da"),
  DUTCH("Dutch", "nld", "nl"),
  ENGLISH("English", "eng", "en"),
  ESTONIAN("Estonian", "est", "et"),
  FINNISH("Finnish", "fin", "fi"),
  FRENCH("French", "fra", "fr"),
  GEORGIAN("Georgian", "kat", "ka"),
  GERMAN("German", "deu", "de"),
  GREEK("Greek", "ell", "el"),
  HUNGARIAN("Hungarian", "hun", "hu"),
  ICELANDIC("Icelandic", "isl", "is"),
  ITALIAN("Italian", "ita", "it"),
  IRISH("Irish", "gle", "ga"),
  JAPANESE("Japanese", "jpn", "ja"),
  KAZAKH("Kazakh", "kaz", "kk"),
  KOREAN("Korean", "kor", "ko"),
  KYRGYZ("Kyrgyz", "kir", "ky"),
  LATIN("Latin", "lat", "la"),
  LATVIAN("Latvian", "lav", "lv"),
  MACEDONIAN("Macedonian", "mkd", "mk"),
  NORWEGIAN("Norwegian", "nor", "no"),
  POLISH("Polish", "pol", "pl"),
  PORTUGUESE("Portuguese", "por", "pt-PT"),
  ROMANIAN("Romanian", "rom", "ro"),
  RUSSIAN("Russian", "rus", "ru"),
  SLOVAK("Slovak", "slk", "sk"),
  SLOVENIAN("Slovenian", "slv", "sl"),
  SPANISH("Spanish", "spa", "es"),
  SERBIAN("Serbian", "srp", "sr"),
  SERBIAN_LATIN("Serbian - Latin", "srp_latn", "sr"),
  TURKISH("Turkish", "tur", "tr"),
  UKRAINIAN("Ukrainian", "ukr", "uk"),
  UZBEK("Uzbek", "uzb", "uz"),
  UZBEK_CYRILLIC("Uzbek - Cyrillic", "uzb_cyrl", "uz")
  ;

  private final String displayName;
  private final String tesseract;
  private final String google;

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
