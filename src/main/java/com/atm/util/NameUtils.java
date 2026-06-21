package com.atm.util;

import com.atm.exception.InvalidInputException;
import java.util.StringJoiner;

public final class NameUtils {

    private NameUtils(){}

    public static String nameFormatter(String name) {

        if (name.isBlank()) {
            throw new InvalidInputException("Name could not be formatted because the name is blank.");
        }

        // Capitalizes the name.
        String[] nameSplit = name.trim().split("\\s+");
        StringJoiner nameJoiner = new StringJoiner(" "); // Appends the StringBuilder inside the loop.

        for (int i = 0; i < nameSplit.length; i++) {
            StringBuilder nameBuilder = new StringBuilder(); //Placeholder for the name to be appended.
            for (int x = 0; x < nameSplit[i].length(); x++) {
                if (x < 1) {
                    nameBuilder.append(Character.toUpperCase(nameSplit[i].charAt(x)));
                } else {
                    nameBuilder.append(Character.toLowerCase(nameSplit[i].charAt(x)));
                }
            }
            nameJoiner.add(nameBuilder.toString());
        }
        return nameJoiner.toString();
    }

    public static String safeDigitTrim(String s) {
        return s == null ? "" : s.replaceAll("\\D", "").trim();
    }

}
