package io.github.eoinkanro.app.rtostranslator.utils;

import java.util.Arrays;

public class LogUtils {

    public static void logError(Throwable e) {
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
    }
}
