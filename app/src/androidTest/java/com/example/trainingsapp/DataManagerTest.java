package com.example.trainingsapp;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.trainingsapp.models.Day;
import com.example.trainingsapp.models.ExerciseDetail;
import com.example.trainingsapp.models.ExerciseSession;
import com.example.trainingsapp.models.ExerciseStatus;
import com.example.trainingsapp.models.RunCardio;
import com.example.trainingsapp.models.StrengthExercise;
import com.example.trainingsapp.models.WorkoutSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentierter Integrationstest für die Klasse DataManager.
 * Validiert die korrekte Funktionsweise der Datenpersistenz, Serialisierung (JSON),
 * Polymorphie-Auflösung sowie des Zustandsmanagements.
 */
@RunWith(AndroidJUnit4.class)
public class DataManagerTest {

    private DataManager dataManager;
    private Context appContext;

    /**
     * Initialisierungsroutine vor jedem Testdurchlauf.
     * Stellt den Android-Application-Context für Zugriffe auf die SharedPreferences bereit
     * und initialisiert die Singleton-Instanz.
     */
    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dataManager = DataManager.getInstance(appContext);
    }

    /**
     * Bereinigungsroutine nach jedem Testdurchlauf.
     * Setzt den internen Cache und die persistenten Speicherdaten zurück,
     * um Seiteneffekte zwischen unabhängigen Tests zu verhindern.
     */
    @After
    public void tearDown() {
        if (dataManager != null) {
            dataManager.resetForTesting();
        }
    }

    /**
     * Validiert den Kopier-Algorithmus (tiefe Kopie) für Trainingseinheiten.
     * Prüft, ob Soll-Werte kopiert und Ist-Werte sowie Status
     * auf ihre definierten Initialzustände zurückgesetzt werden.
     */
    @Test
    public void testCopyExerciseAsPlanned_ResetsActualValues() {
        // Arrange
        RunCardio originalRun = new RunCardio("Marathon Training");
        originalRun.setTargetAveragePaceMinFor1km(5.30f);
        originalRun.setActualAveragePaceMinFor1km(6.00f);
        originalRun.setExerciseStatus(ExerciseStatus.COMPLETED);
        originalRun.setNotes("Analysevermerk");

        // Act
        RunCardio copiedRun = (RunCardio) dataManager.copyExerciseAsPlanned(originalRun);

        // Assert: Name und geplante Werte müssen identisch sein
        assertEquals("Marathon Training", copiedRun.getName());
        assertEquals(5.30f, copiedRun.getTargetAveragePaceMinFor1km(), 0.001);
        assertEquals("Analysevermerk", copiedRun.getNotes());

        // Assert: Ist-Werte müssen zwingend auf Initialzustand zurückgesetzt sein
        assertEquals(0.0f, copiedRun.getActualAveragePaceMinFor1km(), 0.001);
        assertEquals(ExerciseStatus.PLANNED, copiedRun.getExerciseStatus());
    }

    /**
     * Validiert den Kopier-Algorithmus für tief verschachtelte Objektstrukturen.
     * Stellt sicher, dass bei Krafttrainingseinheiten (Listen in Listen) tiefe Kopien
     * erstellt werden und flachen Kopien erstellt werden.
     */
    @Test
    public void testCopyExerciseAsPlanned_StrengthExerciseDeepCopy() {
        // Arrange
        StrengthExercise originalStrength = new StrengthExercise("Push Day");
        originalStrength.addExerciseDetail("Bankdrücken");

        ExerciseDetail benchPress = originalStrength.getExerciseDetails().get(0);
        WorkoutSet firstSet = benchPress.getSets().get(0);

        firstSet.setTargetWeight(100f);
        firstSet.setTargetReps(8);
        firstSet.setActualWeight(95f);
        firstSet.setActualReps(6);

        // Act
        StrengthExercise copiedStrength = (StrengthExercise) dataManager.copyExerciseAsPlanned(originalStrength);

        // Assert: Referenzierung
        ExerciseDetail copiedBenchPress = copiedStrength.getExerciseDetails().get(0);
        WorkoutSet copiedFirstSet = copiedBenchPress.getSets().get(0);

        // Assert A: Speichersicherheit (Vermeidung von Referenzkopplung)
        assertNotEquals("Die Kopie verweist auf denselben Speicherbereich wie das Original.",
                originalStrength.hashCode(), copiedStrength.hashCode());

        // Assert B: Persistenz der Planungsvorgaben
        assertEquals(100f, copiedFirstSet.getTargetWeight(), 0.001);
        assertEquals(8, copiedFirstSet.getTargetReps());

        // Assert C: Rücksetzung der Ist-Werte
        assertEquals(0f, copiedFirstSet.getActualWeight(), 0.001);
        assertEquals(0, copiedFirstSet.getActualReps());
    }

    /**
     * Überprüft die Deserialisierung polymorpher Datentypen durch den Gson-Adapter.
     * Überprüft, ob abstrakte Listen nach einem I/O-Vorgang mit den korrekten
     * spezifischen Subklassen rekonstruiert werden.
     */
    @Test
    public void testPolymorphicSaveAndLoad() {
        // Arrange
        Day testDay = dataManager.getDay("2126-12-31");
        testDay.addRunCardio("Intervalltraining");

        // Act: Persistieren auf der Festplatte
        dataManager.saveAllData();

        // App wird aus dem RAM gewischt (RAM leer, Festplatte bleibt)
        DataManager.simulateAppRestartForTesting();

        // Kaltstart der App: Lädt Daten frisch von der Festplatte in den neuen Singleton
        DataManager freshManager = DataManager.getInstance(appContext);
        Day loadedDay = freshManager.getDay("2126-12-31");

        // Assert
        assertNotNull(loadedDay);
        assertEquals(1, loadedDay.getExercises().size());
        assertTrue("Typenkonflikt: Objekt wurde nicht als korrekte Subklasse instanziiert.",
                loadedDay.getExercises().get(0) instanceof RunCardio);
    }

    /**
     * Verifiziert die Aktualisierung bereits existierender Datensätze.
     * Prüft, ob nachfolgende Speicheroperationen denselben Schlüssel korrekt überschreiben.
     */
    @Test
    public void testSaveAllData_UpdatesExistingRecords() {
        // Arrange
        Day initialDay = dataManager.getDay("2126-05-01");
        initialDay.setNotes("Initiale Dokumentation");
        dataManager.saveAllData();

        // Act: Existierenden Tag abrufen und modifizieren
        Day modifiedDay = dataManager.getDay("2126-05-01");
        modifiedDay.setNotes("Aktualisierte Dokumentation");
        dataManager.saveAllData();

        // App wird aus dem RAM gewischt
        DataManager.simulateAppRestartForTesting();

        // App Start
        DataManager freshManager = DataManager.getInstance(appContext);
        Day finalDay = freshManager.getDay("2126-05-01");

        // Assert
        assertEquals("Der Datensatz wurde im Speicher nicht korrekt aktualisiert.",
                "Aktualisierte Dokumentation", finalDay.getNotes());
    }

    /**
     * Prüft die Fehlertoleranz der JSON-Deserialisierung.
     * Invalide oder beschädigte Speicherinhalte dürfen nicht zum Absturz (Crash) führen,
     * sondern müssen transparent durch leere Initialstrukturen ersetzt werden.
     */
    @Test
    public void testLoadData_WithCorruptedJson_DoesNotCrash() {
        // Arrange: Injektion von syntaktisch fehlerhaftem JSON in die Preferences
        android.content.SharedPreferences prefs = appContext.getSharedPreferences("TrainingsAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("all_days_data", "{ Dies ist eine unzulaessige JSON Struktur! [ }").commit();

        // Act: Initialisierung des Managers mit fehlerhaften Speicherdaten
        dataManager.resetForTesting();
        DataManager freshManager = DataManager.getInstance(appContext);

        // Assert: App-Stabilität und Fallback-Generierung
        Day fallbackDay = freshManager.getDay("2026-03-10");
        assertNotNull("Der Manager muss bei defekter JSON-Struktur ein leeres Fallback-Objekt zurückgeben.", fallbackDay);
    }

    /**
     * Validiert das Entwurfsmuster "Singleton".
     * Stellt sicher, dass das System innerhalb desselben Prozesses immer
     * mit exakt derselben Speicherreferenz des DataManagers arbeitet.
     */
    @Test
    public void testSingletonProperty_ReturnsSameInstance() {
        // Act
        DataManager secondManagerInstance = DataManager.getInstance(appContext);

        // Assert
        assertSame("Singleton-Regelverstoß: Es wurden multiple Instanzen generiert.",
                dataManager, secondManagerInstance);
    }

    /**
     * Prüft die Initialisierung der Trainingstage.
     * Der Abruf eines bislang unbekannten Schlüssels muss automatisch und
     * ohne Laufzeitfehler die Erstellung eines leeren Datenobjekts auslösen.
     */
    @Test
    public void testGetDay_LazyInitializationCreatesNewDay() {
        // Arrange
        String emptyDateKey = "1990-01-01";

        // Act
        Day newlyCreatedDay = dataManager.getDay(emptyDateKey);

        // Assert
        assertNotNull("Verletzung der Lazy Initialization: Resultat ist null.", newlyCreatedDay);
        assertNotNull("Die interne Listenstruktur des Objekts wurde nicht initialisiert.", newlyCreatedDay.getExercises());
        assertEquals("Ein implizit generierter Tag darf initial keine Trainingseinheiten enthalten.", 0, newlyCreatedDay.getExercises().size());
    }

    /**
     * Verifiziert den synchronen Datenaustausch für UI-Komponenten (Activities).
     */
    @Test
    public void testCurrentExerciseBeingEdited_StateIsMaintained() {
        // Arrange
        RunCardio dummyExercise = new RunCardio("Tempotraining");

        // Act
        dataManager.setCurrentExerciseBeingEdited(dummyExercise);
        ExerciseSession retrievedExercise = dataManager.getCurrentExerciseBeingEdited();

        // Assert
        assertNotNull("Die gespeicherte Objektreferenz ist faelschlicherweise null.", retrievedExercise);
        assertEquals("Die referenzierte Instanz stimmt nicht mit dem Ursprung überein.",
                "Tempotraining", retrievedExercise.getName());
    }

    /**
     * Überprüft, ob sich die temporäre Objektreferenz zur Speicheroptimierung
     * ordnungsgemäß auf null zurücksetzen lässt.
     */
    @Test
    public void testSetCurrentExerciseBeingEdited_NullReference_ClearsState() {
        // Arrange
        dataManager.setCurrentExerciseBeingEdited(new RunCardio("Testlauf"));
        assertNotNull(dataManager.getCurrentExerciseBeingEdited());

        // Act
        dataManager.setCurrentExerciseBeingEdited(null);

        // Assert
        assertNull("Die Statusreferenz wurde nicht korrekt freigegeben.", dataManager.getCurrentExerciseBeingEdited());
    }
}