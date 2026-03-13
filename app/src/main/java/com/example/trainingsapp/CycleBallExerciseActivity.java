package com.example.trainingsapp;

import static com.example.trainingsapp.NumberUtils.parseIntOrZero;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trainingsapp.models.CycleBallExercise;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Activity für die Detailansicht und Bearbeitung einer Radball-Trainingseinheit.
 * Bietet eine spezifische Benutzeroberfläche, die auf irrelevante Cardio-Metriken
 * verzichtet und sich stattdessen auf Dauer und Spielanzahl fokussiert.
 */
public class CycleBallExerciseActivity extends AppCompatActivity {

    // Referenz auf das aktuelle Datenobjekt
    private CycleBallExercise currentExercise;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cycleball_exercise);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout_cycleball), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lade das Datenobjekt aus dem DataManager
        currentExercise = (CycleBallExercise) DataManager.getInstance(this).getCurrentExerciseBeingEdited();

        // Initialisiere die UI mit den vorhandenen Daten
        fillViewsFromModel();

        // Konfiguration des Save-Verhaltens beim Verlassen der Activity
        MaterialToolbar toolbar = findViewById(R.id.toolbar_cycleball);
        toolbar.setNavigationOnClickListener(v -> saveAndExit());

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveAndExit();
            }
        });
    }

    /**
     * Führt den Speichervorgang aus, aktualisiert die Daten persistent und
     * navigiert anschließend zur vorherigen Ansicht zurück.
     */
    private void saveAndExit() {
        saveInputsToModel();
        DataManager.getInstance(this).saveAllData();
        Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Befüllt die UI-Elemente mit den Daten des referenzierten Modells.
     * Logische Fallunterscheidung verhindert das Einsetzen von "0" bei leeren Werten.
     */
    private void fillViewsFromModel() {
        // Strings für die Textfelder setzen
        ((EditText) findViewById(R.id.et_cycleball_name)).setText(currentExercise.getName() != null ? currentExercise.getName() : "");
        ((EditText) findViewById(R.id.et_notes)).setText(currentExercise.getNotes() != null ? currentExercise.getNotes() : "");

        // Radball-Felder mit Daten füllen
        if (currentExercise.getTargetDurationMin() > 0)
            ((EditText) findViewById(R.id.et_target_duration)).setText(String.valueOf(currentExercise.getTargetDurationMin()));
        if (currentExercise.getTargetGamesCount() > 0)
            ((EditText) findViewById(R.id.et_target_games)).setText(String.valueOf(currentExercise.getTargetGamesCount()));

        if (currentExercise.getActualDurationMin() > 0)
            ((EditText) findViewById(R.id.et_actual_duration)).setText(String.valueOf(currentExercise.getActualDurationMin()));
        if (currentExercise.getActualGamesCount() > 0)
            ((EditText) findViewById(R.id.et_actual_games)).setText(String.valueOf(currentExercise.getActualGamesCount()));
    }

    /**
     * Liest die aktuellen Benutzereingaben aus der UI aus und schreibt diese
     * (mit Null-Prüfung) zurück in das Datenmodell.
     */
    private void saveInputsToModel() {
        // Text-Felder
        currentExercise.setName(((EditText) findViewById(R.id.et_cycleball_name)).getText().toString());
        currentExercise.setNotes(((EditText) findViewById(R.id.et_notes)).getText().toString());

        // Radball-Felder
        currentExercise.setTargetDurationMin(parseIntOrZero(((EditText) findViewById(R.id.et_target_duration)).getText().toString()));
        currentExercise.setTargetGamesCount(parseIntOrZero(((EditText) findViewById(R.id.et_target_games)).getText().toString()));
        currentExercise.setActualDurationMin(parseIntOrZero(((EditText) findViewById(R.id.et_actual_duration)).getText().toString()));
        currentExercise.setActualGamesCount(parseIntOrZero(((EditText) findViewById(R.id.et_actual_games)).getText().toString()));
    }
}