package net.zithium.deluxecoinflip.utility;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {

    /**
     * Formats a number (integer or double) into a human-readable string with commas.
     *
     * @param number The number to format.
     * @return The formatted string.
     */
    public static String formatNumber(Number number) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(number);
    }

}
