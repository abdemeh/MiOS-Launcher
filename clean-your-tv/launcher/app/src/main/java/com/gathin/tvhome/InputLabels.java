package com.gathin.tvhome;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Presentation only: a physical port never inherits a previously connected device's name. */
final class InputLabels {
    private static final Pattern PORT = Pattern.compile("(?i)^HDMI\\s*(\\d+)$");
    private InputLabels() { }

    static String port(String reportedLabel) {
        Matcher match = PORT.matcher(reportedLabel == null ? "" : reportedLabel.trim());
        return match.matches() ? "HDMI " + match.group(1) : reportedLabel;
    }

    static String device(boolean connected, String childLabel) {
        if (!connected || childLabel == null) return null;
        String label = childLabel.trim().replaceFirst("(?i)^HDMI\\s+", "").trim();
        if (label.isEmpty() || label.matches("\\d+") || label.equalsIgnoreCase("HDMI")) return null;
        return label;
    }
}
