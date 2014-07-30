package com.ambergleam.visualizer.utils;

import org.apache.commons.lang.WordUtils;

public class StringUtils {

    public static String getTimeFormatted(int ms) {
        StringBuilder sb = new StringBuilder();
        int minutes = ((ms / 1000) / 60);
        sb.append(minutes);
        sb.append(":");
        int seconds = ((ms / 1000) % 60);
        if (seconds < 10) {
            sb.append("0");
        }
        sb.append(seconds);
        return sb.toString();
    }

    public static String getFileNameRaw(String name) {
        return name.replace(" - ", "___").replace(' ', '_').toLowerCase();
    }

    public static String getFileNameFormatted(String name) {
        return WordUtils.capitalize(name.replace("___", "_-_").replace('_', ' '));
    }

}
