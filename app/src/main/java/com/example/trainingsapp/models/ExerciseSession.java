package com.example.trainingsapp.models;

/**
 * Die oberste (abstrakte) Basisklasse für alle Trainingseinheiten.
 * Implementiert gemeinsame Eigenschaften, die für jede Sportart (Kraft, Cardio, Radball)
 * Gültigkeit haben.
 */
public abstract class ExerciseSession {

    protected ExerciseType exerciseType;

    // Titel der Einheit (z.B. "Push Day" oder "Morgenlauf")
    protected String name;

    // Subjektives Belastungsempfinden (Rate of Perceived Exertion) der gesamten Einheit
    protected float actualOverallRPE;

    // Freitextfeld für allgemeine Bemerkungen zur Trainingseinheit
    protected String notes;

    protected ExerciseStatus exerciseStatus = ExerciseStatus.PLANNED;

    /**
     * Konstruktor der Basisklasse.
     * Wird durch 'super(name, type)' aus den Konstruktoren der Kindklassen aufgerufen.
     */
    public ExerciseSession(String name, ExerciseType exerciseType) {
        this.name = name;
        this.exerciseType = exerciseType;
    }

    // --- Getter und Setter ---

    public ExerciseStatus getExerciseStatus() {
        return exerciseStatus;
    }

    public void setExerciseStatus(ExerciseStatus status) {
        this.exerciseStatus = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public float getActualOverallRPE() {
        return actualOverallRPE;
    }

    public void setActualOverallRPE(float actualOverallRPE) {
        this.actualOverallRPE = actualOverallRPE;
    }
}