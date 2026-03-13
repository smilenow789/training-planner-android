package com.example.trainingsapp.models;

/**
 * Spezifische Implementierung einer Trainingseinheit für den Radball-Sport.
 * Erbt direkt von der Basisklasse ExerciseSession, da klassische Cardio-Metriken
 * (wie Distanz oder Pace) hier nicht anwendbar sind.
 */
public class CycleBallExercise extends ExerciseSession {

    // Sportartspezifische Metriken für Radball
    private int targetDurationMin;
    private int actualDurationMin;

    private int targetGamesCount;
    private int actualGamesCount;

    /**
     * Konstruktor. Setzt den spezifischen Enum-Typ auf CYCLEBALL.
     */
    public CycleBallExercise(String name) {
        super(name, ExerciseType.CYCLEBALL);
    }

    // --- Getter und Setter für die Datenkapselung ---

    public int getTargetDurationMin() {
        return targetDurationMin;
    }

    public void setTargetDurationMin(int targetDurationMin) {
        this.targetDurationMin = targetDurationMin;
    }

    public int getActualDurationMin() {
        return actualDurationMin;
    }

    public void setActualDurationMin(int actualDurationMin) {
        this.actualDurationMin = actualDurationMin;
    }

    public int getTargetGamesCount() {
        return targetGamesCount;
    }

    public void setTargetGamesCount(int targetGamesCount) {
        this.targetGamesCount = targetGamesCount;
    }

    public int getActualGamesCount() {
        return actualGamesCount;
    }

    public void setActualGamesCount(int actualGamesCount) {
        this.actualGamesCount = actualGamesCount;
    }
}