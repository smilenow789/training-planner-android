package com.example.trainingsapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Spezifische Implementierung einer Trainingseinheit für das Krafttraining.
 * Erbt von der Basisklasse ExerciseSession.
 */
public class StrengthExercise extends ExerciseSession {

    // Liste aller spezifischen Übungen (z. B. Squats, Deadlifts) innerhalb dieser Einheit
    private List<ExerciseDetail> exerciseDetails;

    /**
     * Konstruktor. Ruft den Konstruktor der Basisklasse auf und setzt
     * den Typ explizit auf STRENGTH. Initialisiert zudem die leere Übungsliste.
     */
    public StrengthExercise(String name) {
        super(name, ExerciseType.STRENGTH);
        this.exerciseDetails = new ArrayList<>();
    }

    /**
     * Erstellt eine neue Detail-Übung (z.B. "Bankdrücken") und fügt sie dem Training hinzu.
     *
     * @param exerciseName Der Name der hinzuzufügenden Übung
     */
    public void addExerciseDetail(String exerciseName) {
        ExerciseDetail newDetail = new ExerciseDetail(exerciseName);
        exerciseDetails.add(newDetail);
    }
    
    public List<ExerciseDetail> getExerciseDetails() {
        return exerciseDetails;
    }
}