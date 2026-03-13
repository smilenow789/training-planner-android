package com.example.trainingsapp.models;

/**
 * Konkrete Implementierung einer Ausdauereinheit (CardioExercise) spezifisch für den Radsport.
 * Erweitert die Basisklasse um die radsportspezifische Metrik der
 * Geschwindigkeit (Pace) gemessen in Kilometern pro Stunde (km/h).
 */
public class BicycleCardio extends CardioExercise {

    // Radsportspezifische Metriken (Soll- und Ist-Werte)
    private float targetAveragePaceKmH;
    private float actualAveragePaceKmH;

    /**
     * Konstruktor, der den Namen an die Elternklasse weitergibt.
     *
     * @param name Der Titel der Rad-Einheit (z. B. "Rennrad-Tour")
     */
    public BicycleCardio(String name) {
        super(name);
    }

    // --- Getter und Setter für die Datenkapselung ---

    public float getTargetAveragePaceKmH() {
        return targetAveragePaceKmH;
    }

    public void setTargetAveragePaceKmH(float targetAveragePaceKmH) {
        this.targetAveragePaceKmH = targetAveragePaceKmH;
    }

    public float getActualAveragePaceKmH() {
        return actualAveragePaceKmH;
    }

    public void setActualAveragePaceKmH(float actualAveragePaceKmH) {
        this.actualAveragePaceKmH = actualAveragePaceKmH;
    }
}