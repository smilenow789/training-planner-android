package com.example.trainingsapp.models;

/**
 * Enumeration zur Definition der möglichen Zustände einer Trainingseinheit.
 * Dient der klaren Status-Unterscheidung im User Interface
 */
public enum ExerciseStatus {
    PLANNED,    // Einheit ist für die Zukunft vorgesehen, noch nicht gestartet
    ACTIVE,     // Einheit wird gerade aktiv vom Athleten ausgeführt
    COMPLETED,  // Einheit wurde erfolgreich beendet
    SKIPPED     // Einheit wurde absichtlich ausgelassen (z.B. wegen Krankheit/Regeneration)
}