package com.example.trainingsapp.models;

/**
 * Repräsentiert einen einzelnen Trainingssatz.
 * Enthält die geplante Vorgaben des Trainers (Soll-Werte) als auch die tatsächliche
 * Leistung des Athleten (Ist-Werte) in einer gemeinsamen Datenstruktur.
 */
public class WorkoutSet {

    // --- Geplante Vorgaben (Soll) ---
    private float targetWeight;
    private int targetReps;
    private float targetRir;

    // --- Tatsächlich erreichte Leistung (Ist) ---
    private float actualWeight;
    private int actualReps;
    private float actualRir;

    /**
     * Standard-Konstruktor zur Initialisierung eines neuen Satzes.
     * Beim Erstellen durch den Planer werden zunächst die Zielwerte definiert,
     * während die Ist-Werte durch den Standard-Datentyp bei 0 bleiben.
     */
    public WorkoutSet(float targetWeight, int targetReps, float targetRir) {
        this.targetWeight = targetWeight;
        this.targetReps = targetReps;
        this.targetRir = targetRir;
    }

    // --- Getter und Setter ---

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public int getTargetReps() {
        return targetReps;
    }

    public void setTargetReps(int targetReps) {
        this.targetReps = targetReps;
    }

    public float getTargetRir() {
        return targetRir;
    }

    public void setTargetRir(float targetRir) {
        this.targetRir = targetRir;
    }

    public float getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(float actualWeight) {
        this.actualWeight = actualWeight;
    }

    public int getActualReps() {
        return actualReps;
    }

    public void setActualReps(int actualReps) {
        this.actualReps = actualReps;
    }

    public float getActualRir() {
        return actualRir;
    }

    public void setActualRir(float actualRir) {
        this.actualRir = actualRir;
    }
}