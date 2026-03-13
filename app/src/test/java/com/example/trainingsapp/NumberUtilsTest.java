package com.example.trainingsapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Lokale Unit-Tests zur Verifizierung der statischen Hilfsklasse NumberUtils.
 * Prüft die Fehlertoleranz bei potenziell fehlerhaften Benutzereingaben (Strings).
 */
public class NumberUtilsTest {

    /**
     * Prüft die erfolgreiche Konvertierung von validen Ganzzahlen-Strings.
     * Verifiziert zusätzlich, dass führende oder nachfolgende Leerzeichen (Whitespaces)
     * korrekt getrimmt werden.
     */
    @Test
    public void testParseIntOrZero_ValidNumber_ReturnsNumber() {
        // Assert: Valide Eingabe
        assertEquals(42, NumberUtils.parseIntOrZero("42"));

        // Assert: Eingabe mit störenden Leerzeichen
        assertEquals(42, NumberUtils.parseIntOrZero(" 42 "));
    }

    /**
     * Prüft das Fallback-Verhalten bei Abwesenheit von Werten.
     * Leere Strings, reine Leerzeichen-Strings oder Null-Referenzen müssen
     * sicher abgefangen werden und als 0 resultieren.
     */
    @Test
    public void testParseIntOrZero_EmptyOrNull_ReturnsZero() {
        assertEquals(0, NumberUtils.parseIntOrZero(""));
        assertEquals(0, NumberUtils.parseIntOrZero("   "));
        assertEquals(0, NumberUtils.parseIntOrZero(null));
    }

    /**
     * Prüft die Widerstandsfähigkeit gegenüber fehlerhaften Datentypen.
     * Strings mit Buchstaben oder Dezimalpunkten dürfen keinen App-Crash auslösen.
     */
    @Test
    public void testParseIntOrZero_InvalidFormat_ReturnsZero() {
        assertEquals(0, NumberUtils.parseIntOrZero("12abc"));
        assertEquals(0, NumberUtils.parseIntOrZero("12.5")); // int erlaubt keine Dezimalstellen
    }

    /**
     * Prüft die Konvertierung von validen Gleitkommazahlen.
     */
    @Test
    public void testParseFloatOrZero_ValidNumber_ReturnsNumber() {
        assertEquals(12.5f, NumberUtils.parseFloatOrZero("12.5"), 0.001f);
        assertEquals(42f, NumberUtils.parseFloatOrZero("  42  "), 0.001f);
    }

    /**
     * Prüft das Fallback-Verhalten für Gleitkommazahlen bei leeren Eingaben.
     */
    @Test
    public void testParseFloatOrZero_EmptyOrNull_ReturnsZero() {
        assertEquals(0f, NumberUtils.parseFloatOrZero(""), 0.001f);
        assertEquals(0f, NumberUtils.parseFloatOrZero("   "), 0.001f);
        assertEquals(0f, NumberUtils.parseFloatOrZero(null), 0.001f);
    }

    /**
     * Prüft die Wiederstandsfähigkeit bei Gleitkommazahlen.
     */
    @Test
    public void testParseFloatOrZero_InvalidFormat_ReturnsZero() {
        assertEquals(0f, NumberUtils.parseFloatOrZero("12,5"), 0.001f);
        assertEquals(0f, NumberUtils.parseFloatOrZero("12.5abc"), 0.001f);
    }
}