package com.example.trainingsapp;

import static com.example.trainingsapp.NumberUtils.*;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trainingsapp.models.BicycleCardio;
import com.example.trainingsapp.models.CardioExercise;
import com.example.trainingsapp.models.RunCardio;
import com.example.trainingsapp.models.SwimCardio;
import com.google.android.material.appbar.MaterialToolbar;


/**
 * Die Activity für alle Cardio-basierten Trainingseinheiten.
 */
public class CardioExerciseActivity extends AppCompatActivity {

    // Hält die Referenz auf das aktuelle Datenobjekt
    private CardioExercise currentExercise;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cardio_exercise);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout_cardio), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lade das Datenobjekt aus dem DataManager
        currentExercise = (CardioExercise) DataManager.getInstance(this).getCurrentExerciseBeingEdited();

        // Initialisiere die UI mit den vorhandenen Daten
        fillViewsFromModel();

        // Konfiguration des Save-Verhaltens beim Verlassen der Activity
        MaterialToolbar toolbar = findViewById(R.id.toolbar_cardio);
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
     * Befüllt die UI-Elemente mit Daten aus dem Model.
     * Beinhaltet eine Logik, um Nullwerte auszublenden
     * und das spezifische Label für die Geschwindigkeit (Pace) dynamisch anzupassen.
     */
    private void fillViewsFromModel() {
        // Strings für die Textfelder sicher setzen
        ((EditText) findViewById(R.id.et_cardio_name)).setText(currentExercise.getName() != null ? currentExercise.getName() : "");
        ((EditText) findViewById(R.id.et_warmup)).setText(currentExercise.getWarmup() != null ? currentExercise.getWarmup() : "");
        ((EditText) findViewById(R.id.et_main)).setText(currentExercise.getMain() != null ? currentExercise.getMain() : "");
        ((EditText) findViewById(R.id.et_cooldown)).setText(currentExercise.getCoolDown() != null ? currentExercise.getCoolDown() : "");
        ((EditText) findViewById(R.id.et_notes)).setText(currentExercise.getNotes() != null ? currentExercise.getNotes() : "");

        // Allgemeine Cardio-Felder (Soll)
        if (currentExercise.getTargetDurationMin() > 0)
            ((EditText) findViewById(R.id.et_target_duration)).setText(String.valueOf(currentExercise.getTargetDurationMin()));
        if (currentExercise.getTargetDistanceKm() > 0)
            ((EditText) findViewById(R.id.et_target_distance)).setText(String.valueOf(currentExercise.getTargetDistanceKm()));
        if (currentExercise.getTargetAverageHeartRate() > 0)
            ((EditText) findViewById(R.id.et_target_avg_hr)).setText(String.valueOf(currentExercise.getTargetAverageHeartRate()));
        if (currentExercise.getTargetMaxHeartRate() > 0)
            ((EditText) findViewById(R.id.et_target_max_hr)).setText(String.valueOf(currentExercise.getTargetMaxHeartRate()));
        if (currentExercise.getTargetOverallRPE() > 0)
            ((EditText) findViewById(R.id.et_target_rpe)).setText(String.valueOf(currentExercise.getTargetOverallRPE()));

        // Allgemeine Cardio-Felder (Ist)
        if (currentExercise.getActualDurationMin() > 0)
            ((EditText) findViewById(R.id.et_actual_duration)).setText(String.valueOf(currentExercise.getActualDurationMin()));
        if (currentExercise.getActualDistanceKm() > 0)
            ((EditText) findViewById(R.id.et_actual_distance)).setText(String.valueOf(currentExercise.getActualDistanceKm()));
        if (currentExercise.getActualAverageHeartRate() > 0)
            ((EditText) findViewById(R.id.et_actual_avg_hr)).setText(String.valueOf(currentExercise.getActualAverageHeartRate()));
        if (currentExercise.getActualMaxHeartRate() > 0)
            ((EditText) findViewById(R.id.et_actual_max_hr)).setText(String.valueOf(currentExercise.getActualMaxHeartRate()));
        if (currentExercise.getActualOverallRPE() > 0)
            ((EditText) findViewById(R.id.et_actual_rpe)).setText(String.valueOf(currentExercise.getActualOverallRPE()));


        // Prüft, welche konkrete Kindklasse vorliegt, passt die UI-Beschriftung an
        // und castet das Objekt, um auf spezifische Getter zuzugreifen.
        TextView tvPaceLabel = findViewById(R.id.tv_pace_label);
        EditText etTargetPace = findViewById(R.id.et_target_pace);
        EditText etActualPace = findViewById(R.id.et_actual_pace);

        if (currentExercise instanceof SwimCardio) {
            tvPaceLabel.setText(R.string.pace_label_swim);
            SwimCardio swim = (SwimCardio) currentExercise;
            if (swim.getTargetAveragePaceMinFor100M() > 0)
                etTargetPace.setText(String.valueOf(swim.getTargetAveragePaceMinFor100M()));
            if (swim.getActualAveragePaceMinFor100M() > 0)
                etActualPace.setText(String.valueOf(swim.getActualAveragePaceMinFor100M()));

        } else if (currentExercise instanceof RunCardio) {
            tvPaceLabel.setText(R.string.pace_label_run);
            RunCardio run = (RunCardio) currentExercise;
            if (run.getTargetAveragePaceMinFor1km() > 0)
                etTargetPace.setText(String.valueOf(run.getTargetAveragePaceMinFor1km()));
            if (run.getActualAveragePaceMinFor1km() > 0)
                etActualPace.setText(String.valueOf(run.getActualAveragePaceMinFor1km()));

        } else if (currentExercise instanceof BicycleCardio) {
            tvPaceLabel.setText(R.string.pace_label_bike);
            BicycleCardio bike = (BicycleCardio) currentExercise;
            if (bike.getTargetAveragePaceKmH() > 0)
                etTargetPace.setText(String.valueOf(bike.getTargetAveragePaceKmH()));
            if (bike.getActualAveragePaceKmH() > 0)
                etActualPace.setText(String.valueOf(bike.getActualAveragePaceKmH()));
        }
    }

    /**
     * Liest die UI-Eingaben aus und aktualisiert das entsprechende Datenobjekt des Models.
     * Verwendet Hilfsmethoden, um NumberFormatExceptions durch leere Felder zu vermeiden.
     */
    private void saveInputsToModel() {
        // Text-Felder
        currentExercise.setName(((EditText) findViewById(R.id.et_cardio_name)).getText().toString());
        currentExercise.setWarmup(((EditText) findViewById(R.id.et_warmup)).getText().toString());
        currentExercise.setMain(((EditText) findViewById(R.id.et_main)).getText().toString());
        currentExercise.setCoolDown(((EditText) findViewById(R.id.et_cooldown)).getText().toString());
        currentExercise.setNotes(((EditText) findViewById(R.id.et_notes)).getText().toString());

        // Allgemeine Cardio-Felder (Soll)
        currentExercise.setTargetDurationMin(parseIntOrZero(((EditText) findViewById(R.id.et_target_duration)).getText().toString()));
        currentExercise.setTargetDistanceKm(parseFloatOrZero(((EditText) findViewById(R.id.et_target_distance)).getText().toString()));
        currentExercise.setTargetAverageHeartRate(parseIntOrZero(((EditText) findViewById(R.id.et_target_avg_hr)).getText().toString()));
        currentExercise.setTargetMaxHeartRate(parseIntOrZero(((EditText) findViewById(R.id.et_target_max_hr)).getText().toString()));
        currentExercise.setTargetOverallRPE(parseFloatOrZero(((EditText) findViewById(R.id.et_target_rpe)).getText().toString()));

        // Allgemeine Cardio-Felder (Ist)
        currentExercise.setActualDurationMin(parseIntOrZero(((EditText) findViewById(R.id.et_actual_duration)).getText().toString()));
        currentExercise.setActualDistanceKm(parseFloatOrZero(((EditText) findViewById(R.id.et_actual_distance)).getText().toString()));
        currentExercise.setActualAverageHeartRate(parseIntOrZero(((EditText) findViewById(R.id.et_actual_avg_hr)).getText().toString()));
        currentExercise.setActualMaxHeartRate(parseIntOrZero(((EditText) findViewById(R.id.et_actual_max_hr)).getText().toString()));
        currentExercise.setActualOverallRPE(parseFloatOrZero(((EditText) findViewById(R.id.et_actual_rpe)).getText().toString()));

        // DYNAMISCHE PACE/GESCHWINDIGKEIT LOGIK
        float targetPace = parseFloatOrZero(((EditText) findViewById(R.id.et_target_pace)).getText().toString());
        float actualPace = parseFloatOrZero(((EditText) findViewById(R.id.et_actual_pace)).getText().toString());

        if (currentExercise instanceof SwimCardio) {
            ((SwimCardio) currentExercise).setTargetAveragePaceMinFor100M(targetPace);
            ((SwimCardio) currentExercise).setActualAveragePaceMinFor100M(actualPace);

        } else if (currentExercise instanceof RunCardio) {
            ((RunCardio) currentExercise).setTargetAveragePaceMinFor1km(targetPace);
            ((RunCardio) currentExercise).setActualAveragePaceMinFor1km(actualPace);

        } else if (currentExercise instanceof BicycleCardio) {
            ((BicycleCardio) currentExercise).setTargetAveragePaceKmH(targetPace);
            ((BicycleCardio) currentExercise).setActualAveragePaceKmH(actualPace);
        }
    }
}