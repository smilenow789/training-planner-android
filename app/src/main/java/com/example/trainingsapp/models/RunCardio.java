package com.example.trainingsapp.models;

/**
 * Konkrete Implementierung einer Ausdauereinheit (CardioExercise) spezifisch für den Laufsport.
 * Erweitert die generische Basisklasse um die laufsportspezifische Metrik der
 * Pace, gemessen in Minuten pro Kilometer (min/1km).
 */
public class RunCardio extends CardioExercise {

    // Laufspezifische Metriken (Soll- und Ist-Werte)
    private float targetAveragePaceMinFor1km;
    private float actualAveragePaceMinFor1km;

    /**
     * Konstruktor, der den Namen an die abstrakte Elternklasse weitergibt.
     *
     * @param name Der Titel der Laufeinheit (z. B. "Intervalllauf")
     */
    public RunCardio(String name) {
        super(name);
    }

    // --- Getter und Setter ---

    public float getTargetAveragePaceMinFor1km() {
        return targetAveragePaceMinFor1km;
    }

    public void setTargetAveragePaceMinFor1km(float targetAveragePaceMinFor1km) {
        this.targetAveragePaceMinFor1km = targetAveragePaceMinFor1km;
    }

    public float getActualAveragePaceMinFor1km() {
        return actualAveragePaceMinFor1km;
    }

    public void setActualAveragePaceMinFor1km(float actualAveragePaceMinFor1km) {
        this.actualAveragePaceMinFor1km = actualAveragePaceMinFor1km;
    }
}