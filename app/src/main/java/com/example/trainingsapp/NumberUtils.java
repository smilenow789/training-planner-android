package com.example.trainingsapp;

/**
 * Hilfsklasse für numerische Werte.
 * Enthält Hilfsmethoden für Ganzzahlen und Gleitkommazahlen.
 */
public class NumberUtils {

    /**
     * Hilfsmethode für Ganzzahlen.
     * Beugt App-Abstürzen (Crashes) vor, falls der Nutzer Felder leer lässt oder löscht.
     *
     * @return Die eingegebene Zahl oder 0 bei Fehler/Leere.
     */
    public static int parseIntOrZero(String input) {
        if (input == null || input.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Hilfsmethode für Gleitkommazahlen.
     * Beugt App-Abstürzen (Crashes) vor, falls der Nutzer Felder leer lässt oder löscht.
     *
     * @return Die eingegebene Zahl oder 0f bei Fehler/Leere.
     */
    public static float parseFloatOrZero(String input) {
        if (input == null || input.trim().isEmpty()) return 0f;
        try {
            return Float.parseFloat(input.trim());
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
}