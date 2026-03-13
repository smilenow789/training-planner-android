package com.example.trainingsapp.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repräsentiert einen einzelnen Kalendertag im Trainingsplan.
 * Fungiert als Container zur Verwaltung einer Liste von an diesem Tag
 * geplanten Trainingseinheiten.
 */
public class Day {

    // Datum als primäres Identifikationsmerkmal
    private Date date;

    // Allgemeine, tagesübergreifende Anweisungen des Trainers (z. B. "Schlafqualität beachten")
    private String notes = "";

    // Liste, die alle Arten von Trainingseinheiten (Kraft, Cardio, Radball)
    // durch die Nutzung der gemeinsamen Basisklasse ExerciseSession aufnehmen kann.
    private List<ExerciseSession> exercises;

    /**
     * Konstruktor zur Initialisierung eines leeren Trainingstages.
     *
     * @param date Das spezifische Datum des Tages
     */
    public Day(Date date) {
        this.date = date;
        this.exercises = new ArrayList<>();
    }

    // Die folgenden Methoden instanziieren spezifische Subklassen und fügen sie
    // der allgemeinen ExerciseSession-Liste hinzu (Upcasting).

    public void addStrengthExercise(String name) {
        StrengthExercise newExercise = new StrengthExercise(name);
        exercises.add(newExercise);
    }

    public void addSwimCardio(String name) {
        SwimCardio newExercise = new SwimCardio(name);
        exercises.add(newExercise);
    }

    public void addRunCardio(String name) {
        RunCardio newExercise = new RunCardio(name);
        exercises.add(newExercise);
    }

    public void addBicycleCardio(String name) {
        BicycleCardio newExercise = new BicycleCardio(name);
        exercises.add(newExercise);
    }

    public void addCycleBallExercise(String name) {
        CycleBallExercise newExercise = new CycleBallExercise(name);
        exercises.add(newExercise);
    }

    // --- Getter und Setter für die Datenkapselung ---

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<ExerciseSession> getExercises() {
        return exercises;
    }

    public Date getDate() {
        return date;
    }
}