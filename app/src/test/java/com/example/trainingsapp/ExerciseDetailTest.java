package com.example.trainingsapp;

import com.example.trainingsapp.models.ExerciseDetail;
import com.example.trainingsapp.models.WorkoutSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Lokale Unit-Tests innerhalb der ExerciseDetail.
 */
public class ExerciseDetailTest {

    /**
     * Prüft die Initialisierungslogik des Konstruktors.
     * Eine neue Übung muss standardmäßig mit der korrekten Anzahl an leeren
     * Sätzen (standardmäßig mit 1) vorbereitet werden.
     */
    @Test
    public void testConstructor_InitializesCorrectNumberOfSets() {
        // Arrange & Act
        ExerciseDetail detail = new ExerciseDetail("Kreuzheben");

        // Assert: Standardmäßig ist targetSetCount = 1, daher muss 1 Satz existieren
        assertEquals(1, detail.getSets().size());
    }

    /**
     * Validiert das standardmäßige Hinzufügen von neuen Sätzen zur internen Liste.
     */
    @Test
    public void testAddWorkoutSet_IncreasesListSize() {
        // Arrange
        ExerciseDetail detail = new ExerciseDetail("Klimmzüge");
        int initialSize = detail.getSets().size(); // Sollte 1 sein

        // Act
        detail.addWorkoutSet(new WorkoutSet(0, 0, 0));

        // Assert
        assertEquals(initialSize + 1, detail.getSets().size());
    }

    /**
     * Validiert das korrekte Entfernen des chronologisch zuletzt hinzugefügten Satzes.
     */
    @Test
    public void testRemoveWorkoutSet_PopulatedList_DecreasesSize() {
        // Arrange: Übung hat durch den Konstruktor initial 1 Satz
        ExerciseDetail detail = new ExerciseDetail("Bankdrücken");

        // Verändern des ersten Satz, um später zu prüfen, ob dieser bestehen bleibt.
        detail.getSets().get(0).setTargetWeight(60f);

        // Zweiten Satz hinzufügen
        detail.addWorkoutSet(new WorkoutSet(1, 1, 1));

        // Act: Der zweite Satz (der zuletzt hinzugefügte) sollte entfernt werden
        detail.removeWorkoutSet();

        // Assert: Die Liste muss nun 1 sein
        assertEquals(1, detail.getSets().size());

        // Assert: Der verbliebene Satz muss der erste Satz mit den 60kg sein!
        assertEquals(60f, detail.getSets().get(0).getTargetWeight(), 0.001f);
    }

    /**
     * Prüft das Grenzverhalten (Edge Case / Defensive Programming).
     * Der Versuch, ein Element aus einer bereits leeren Liste zu löschen,
     * darf keine IndexOutOfBoundsException auslösen.
     */
    @Test
    public void testRemoveWorkoutSet_EmptyList_DoesNotCrash() {
        // Arrange: Übung erstellen und manuell leeren
        ExerciseDetail detail = new ExerciseDetail("Kniebeugen");
        detail.removeWorkoutSet();

        // Verifikation der Ausgangslage: Liste ist definitiv leer
        assertEquals(0, detail.getSets().size());

        // Act: Simulierter Nutzerklick auf "Satz entfernen", obwohl keine Sätze existieren
        detail.removeWorkoutSet();

        // Assert: Die Schutzlogik greift, die App stürzt nicht ab, die Größe bleibt 0
        assertEquals(0, detail.getSets().size());
    }
}