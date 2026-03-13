package com.example.trainingsapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert eine einzelne, spezifische Übung (z. B. "Bankdrücken") innerhalb
 * einer übergeordneten Krafttrainingseinheit (StrengthExercise).
 * Fungiert als Bindeglied zwischen der Trainingseinheit und
 * den dazugehörigen individuellen Trainingssätzen (WorkoutSet).
 */
public class ExerciseDetail {

    private String exerciseName;

    // Die geplante Anzahl an Trainings-Sätzen für diese spezifische Übung.
    // Dient als Soll-Vorgabe für den Athleten.
    private int targetSetCount = 1;

    // Liste der Trainings-Sätze (Soll/Ist-Werte pro Satz).
    private List<WorkoutSet> sets;

    /**
     * Konstruktor zur Initialisierung einer neuen Übungs-Detailansicht.
     * Generiert beim Erstellen automatisch die geplante Anzahl an leeren Sätzen,
     * um die Benutzeroberfläche direkt mit Eingabefeldern vorzubereiten.
     *
     * @param exerciseName Der Name der Kraftübung
     */
    public ExerciseDetail(String exerciseName) {
        this.exerciseName = exerciseName;
        this.sets = new ArrayList<>();

        // Vorbefüllen der Liste mit leeren Sätzen basierend auf dem targetSetCount
        for (int i = 0; i < targetSetCount; i++) {
            addWorkoutSet(new WorkoutSet(0, 0, 0));
        }
    }

    // --- Getter und Setter ---

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String name) {
        this.exerciseName = name;
    }

    public int getTargetSetCount() {
        return targetSetCount;
    }

    public void setTargetSetCount(int newCount) {
        this.targetSetCount = newCount;
    }

    /**
     * Fügt einen neuen Satz (WorkoutSet) zur Übung hinzu.
     * Ermöglicht dem Athleten, spontan mehr Sätze zu absolvieren als ursprünglich geplant.
     */
    public void addWorkoutSet(WorkoutSet set) {
        sets.add(set);
    }

    /**
     * Entfernt den zuletzt hinzugefügten Satz aus der Liste.
     * Wird genutzt, wenn der Athlet weniger Sätze absolviert als geplant oder
     * Eingabefehler korrigieren möchte.
     */
    public void removeWorkoutSet() {
        if (!sets.isEmpty()) {
            sets.remove(sets.size() - 1);
        }
    }

    public List<WorkoutSet> getSets() {
        return sets;
    }
}