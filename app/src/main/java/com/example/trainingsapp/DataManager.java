package com.example.trainingsapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.trainingsapp.models.BicycleCardio;
import com.example.trainingsapp.models.CardioExercise;
import com.example.trainingsapp.models.CycleBallExercise;
import com.example.trainingsapp.models.Day;
import com.example.trainingsapp.models.ExerciseDetail;
import com.example.trainingsapp.models.ExerciseSession;
import com.example.trainingsapp.models.ExerciseStatus;
import com.example.trainingsapp.models.RunCardio;
import com.example.trainingsapp.models.StrengthExercise;
import com.example.trainingsapp.models.SwimCardio;
import com.example.trainingsapp.models.WorkoutSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

/**
 * Der DataManager fungiert als zentraler Controller für die Datenhaltung der Applikation.
 * Implementiert als Singleton, um eine konsistente Datenbasis
 * über alle Activities hinweg zu gewährleisten.
 */
public class DataManager {

    private static DataManager instance;
    private SharedPreferences prefs;
    private Gson gson;

    // Speichert alle Trainingstage als Key-Value-Paar (Datum-String -> Day-Objekt)
    private HashMap<String, Day> allDaysMap;

    // Temporärer Speicher, um das aktuell ausgewählte Übungsobjekt zwischen Activities zu übergeben.
    private ExerciseSession currentExerciseBeingEdited;

    /**
     * Privater Konstruktor (Singleton).
     * Verhindert die mehrfache Instanziierung von außen.
     */
    private DataManager(Context context) {
        // Initialisierung der SharedPreferences für den lokalen Speicherzugriff
        prefs = context.getSharedPreferences("TrainingsAppPrefs", Context.MODE_PRIVATE);

        // Konfiguration von Gson, um die komplexen Datenstrukturen zu serialisieren
        gson = new GsonBuilder()
                .registerTypeAdapter(ExerciseSession.class, new ExerciseSessionAdapter())
                .create();

        // Lade vorhandene Daten beim Initialisieren in den Arbeitsspeicher
        loadData();
    }

    /**
     * Abfrage der Singleton-Instanz.
     */
    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Hilfsmethode ausschließlich für automatisierte Tests.
     * Zum Simulieren des Beenden der Applikation.
     */
    public static void simulateAppRestartForTesting() {
        instance = null;
    }

    /**
     * Lädt die gespeicherten Daten aus den SharedPreferences.
     * Konvertiert den JSON-String mittels Gson in die HashMap.
     */
    private void loadData() {
        String jsonString = prefs.getString("all_days_data", null);
        if (jsonString != null) {
            // Definiert den komplexen Datentyp für das Deserialisieren
            Type type = new TypeToken<HashMap<String, Day>>() {
            }.getType();
            allDaysMap = gson.fromJson(jsonString, type);
        } else {
            // Fallback bei leerem Speicher (z. B. beim allerersten App-Start)
            allDaysMap = new HashMap<>();
        }
    }

    /**
     * Speichert den aktuellen Zustand persistent ab.
     * Wandelt die HashMap in einen JSON-String um und schreibt diesen in die SharedPreferences.
     */
    public void saveAllData() {
        String jsonString = gson.toJson(allDaysMap);
        prefs.edit().putString("all_days_data", jsonString).apply();
    }

    // --- Getter & Setter für den Activity-Datenaustausch ---

    /**
     * Gibt das Day-Objekt für ein spezifisches Datum zurück.
     * Wenn für das angefragte Datum noch kein Objekt existiert,
     * wird ein leerer Tag erstellt.
     *
     * @param dateString Das Datum als Schlüssel (z. B. "2026-03-10")
     * @return Das dazugehörige Day-Objekt
     */
    public Day getDay(String dateString) {
        if (!allDaysMap.containsKey(dateString)) {
            allDaysMap.put(dateString, new Day(new Date()));
        }
        return allDaysMap.get(dateString);
    }

    public ExerciseSession getCurrentExerciseBeingEdited() {
        return currentExerciseBeingEdited;
    }

    public void setCurrentExerciseBeingEdited(ExerciseSession exercise) {
        this.currentExerciseBeingEdited = exercise;
    }

    /**
     * Erstellt eine exakte Kopie einer Übungseinheit (z. B. für das Kopieren einer Vorwoche),
     * bei der alle "Ist-Werte" zurückgesetzt und der Status auf "Geplant" gesetzt werden.
     *
     * @param original Die zu kopierende Übungseinheit
     * @return Ein neues Objekt mit zurückgesetzten Ist-Werten
     */
    public ExerciseSession copyExerciseAsPlanned(ExerciseSession original) {
        String json = this.gson.toJson(original, ExerciseSession.class);
        ExerciseSession copy = this.gson.fromJson(json, ExerciseSession.class);

        // Grundlegende Metriken der Elternklasse zurücksetzen
        copy.setExerciseStatus(ExerciseStatus.PLANNED);
        copy.setActualOverallRPE(0);

        // Klassenspezifische Ist-Werte (Actual) durch Type-Checking und Casting zurücksetzen
        if (copy instanceof CardioExercise) {
            CardioExercise c = (CardioExercise) copy;
            c.setActualDurationMin(0);
            c.setActualDistanceKm(0);
            c.setActualAverageHeartRate(0);
            c.setActualMaxHeartRate(0);

            // Spezifische Pace-Metriken der Kindklassen zurücksetzen
            if (c instanceof SwimCardio) ((SwimCardio) c).setActualAveragePaceMinFor100M(0);
            if (c instanceof RunCardio) ((RunCardio) c).setActualAveragePaceMinFor1km(0);
            if (c instanceof BicycleCardio) ((BicycleCardio) c).setActualAveragePaceKmH(0);

        } else if (copy instanceof CycleBallExercise) {
            CycleBallExercise cb = (CycleBallExercise) copy;
            cb.setActualDurationMin(0);
            cb.setActualGamesCount(0);

        } else if (copy instanceof StrengthExercise) {
            StrengthExercise s = (StrengthExercise) copy;
            // Durchlaufen der verschachtelten Datenstruktur (Details -> Sets),
            // um alle Werte auf 0 zu setzten
            for (ExerciseDetail detail : s.getExerciseDetails()) {
                for (WorkoutSet set : detail.getSets()) {
                    set.setActualWeight(0);
                    set.setActualReps(0);
                    set.setActualRir(0);
                }
            }
        }
        return copy;
    }

    /**
     * Hilfsmethode ausschließlich für automatisierte Tests.
     * Löscht den gesamten In-Memory-Cache und leert die SharedPreferences,
     * um eine saubere Ausgangslage für nachfolgende Tests zu garantieren.
     */
    public void resetForTesting() {
        if (allDaysMap != null) {
            allDaysMap.clear();
        }
        if (prefs != null) {
            prefs.edit().clear().commit();
        }
        currentExerciseBeingEdited = null;
        // Der Singleton muss komplett zerstört werden,
        // damit beim nächsten Test ein frisches loadData() ausgeführt wird!
        instance = null;
    }

    /**
     * Gson Adapter zur Serialisierung/Deserialisierung von Polymorphie.
     * Da Gson abstrakte Klassen (ExerciseSession) nicht standardmäßig instanziieren kann,
     * muss beim Speichern der konkrete Klassentyp als Meta-Information in das JSON injiziert
     * und beim Laden wieder ausgelesen werden.
     */
    private static class ExerciseSessionAdapter implements JsonSerializer<ExerciseSession>, JsonDeserializer<ExerciseSession> {

        @Override
        public JsonElement serialize(ExerciseSession src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = context.serialize(src, src.getClass()).getAsJsonObject();
            // Speichert den exakten Klassennamen (z.B. "SwimCardio") als zusätzliches JSON-Feld ab
            result.addProperty("CLASS_META_KEY", src.getClass().getSimpleName());
            return result;
        }

        @Override
        public ExerciseSession deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            // Liest die gespeicherte Metainformation aus
            String className = jsonObject.get("CLASS_META_KEY").getAsString();

            try {
                // Weist Gson an, das JSON-Objekt anhand des ausgelesenen Namens in die
                // korrekte, spezifische Kindklasse umzuwandeln.
                switch (className) {
                    case "StrengthExercise":
                        return context.deserialize(jsonObject, StrengthExercise.class);
                    case "SwimCardio":
                        return context.deserialize(jsonObject, SwimCardio.class);
                    case "RunCardio":
                        return context.deserialize(jsonObject, RunCardio.class);
                    case "BicycleCardio":
                        return context.deserialize(jsonObject, BicycleCardio.class);
                    case "CycleBallExercise":
                        return context.deserialize(jsonObject, CycleBallExercise.class);
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            return null;
        }
    }
}