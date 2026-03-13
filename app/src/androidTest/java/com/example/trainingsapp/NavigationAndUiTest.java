package com.example.trainingsapp;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.trainingsapp.models.Day;
import com.example.trainingsapp.models.RunCardio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Automatisierte UI-Tests (Espresso) zur Verifizierung der Benutzeroberfläche.
 */
@RunWith(AndroidJUnit4.class)
public class NavigationAndUiTest {

    private DataManager dataManager;

    /**
     * Setup-Routine zur Vorbereitung der Testumgebung.
     * Initialisiert den Singleton-DataManager und erzwingt eine Bereinigung
     * des persistenten Speichers, um Seiteneffekte zwischen Tests auszuschließen.
     */
    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dataManager = DataManager.getInstance(appContext);
        dataManager.resetForTesting();
    }

    /**
     * Teardown-Routine zur Freigabe von Ressourcen.
     * Leeren Systemzustand nach Abschluss der Testausführung.
     */
    @After
    public void tearDown() {
        dataManager.resetForTesting();
    }

    /**
     * Überprüft, ob Daten, die programmgesteuert im Datenmodell hinterlegt wurden,
     * nach dem Start der Activity fehlerfrei in der UI gerendert werden.
     */
    @Test
    public void testRecyclerView_DisplaysAddedExercise() {
        // Arrange: Injektion eines spezifischen Testdatensatzes in den lokalen Speicher
        Day today = dataManager.getDay("2226-01-01");
        today.addRunCardio("Morgenlauf (Test)");
        dataManager.saveAllData();

        // Act: Konfiguration und Start der Activity über ein Intent
        android.content.Intent startIntent = new android.content.Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                DayActivity.class);
        startIntent.putExtra("EXTRA_DATE", "2226-01-01");

        try (ActivityScenario<DayActivity> scenario = ActivityScenario.launch(startIntent)) {

            // Assert: Verifikation der UI-Darstellung mittels ViewMatcher.
            // Erwartet wird die Sichtbarkeit des zuvor injizierten Datensatzes.
            Espresso.onView(ViewMatchers.withText("Morgenlauf (Test)"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    /**
     * Überprüft die applikationsinterne Navigation durch den ExerciseAdapter.
     * Simuliert ein Click-Event auf ein RecyclerView-Element und verifiziert die
     * korrekte Zuweisung der Ziel-Activity anhand des polymorphen Datentyps.
     */
    @Test
    public void testClickOnCardioExercise_NavigatesToCardioActivity() {
        // Arrange: Präparation des Datenbestandes (Anlegen einer Ausdauereinheit)
        Day today = dataManager.getDay("2326-01-01");
        today.getExercises().add(new RunCardio("Test-Marathon"));
        dataManager.saveAllData();

        // Initialisiere den Intent-Monitor
        Intents.init();

        android.content.Intent startIntent = new android.content.Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                DayActivity.class);
        startIntent.putExtra("EXTRA_DATE", "2326-01-01");

        try (ActivityScenario<DayActivity> scenario = ActivityScenario.launch(startIntent)) {

            // Act: Ausführung einer simulierten Benutzerinteraktion (Click) auf den ersten Listen-Eintrag
            Espresso.onView(ViewMatchers.withId(R.id.recyclerView_exercises))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

            // Assert: Verifikation des abgesetzten Intents auf Übereinstimmung mit der erwarteten Ziel-Komponente
            Intents.intended(IntentMatchers.hasComponent(CardioExerciseActivity.class.getName()));

        } finally {
            Intents.release();
        }
    }
}