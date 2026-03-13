package com.example.trainingsapp.models;

/**
 * Konkrete Implementierung einer Ausdauereinheit (CardioExercise) spezifisch für den Schwimmsport.
 * Erweitert die generische Basisklasse um die schwimmspezifische Metrik der
 * Pace, gemessen in Minuten pro 100 Meter (min/100m).
 */
public class SwimCardio extends CardioExercise {

    // Schwimmspezifische Metriken (Soll- und Ist-Werte)
    private float targetAveragePaceMinFor100M;
    private float actualAveragePaceMinFor100M;

    /**
     * Konstruktor, der den Namen an die abstrakte Elternklasse weitergibt.
     *
     * @param name Der Titel der Schwimmeinheit (z. B. "Grundlagenausdauer Pool")
     */
    public SwimCardio(String name) {
        super(name);
    }

    // --- Getter und Setter ---

    public float getTargetAveragePaceMinFor100M() {
        return targetAveragePaceMinFor100M;
    }

    public void setTargetAveragePaceMinFor100M(float targetAveragePaceMinFor100M) {
        this.targetAveragePaceMinFor100M = targetAveragePaceMinFor100M;
    }

    public float getActualAveragePaceMinFor100M() {
        return actualAveragePaceMinFor100M;
    }

    public void setActualAveragePaceMinFor100M(float actualAveragePaceMinFor100M) {
        this.actualAveragePaceMinFor100M = actualAveragePaceMinFor100M;
    }
}