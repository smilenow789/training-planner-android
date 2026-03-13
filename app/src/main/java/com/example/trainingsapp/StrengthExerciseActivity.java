package com.example.trainingsapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainingsapp.models.StrengthExercise;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;

/**
 * Activity für die Detailansicht und Bearbeitung einer Krafttrainingseinheit.
 * Bietet eine Benutzeroberfläche zur Verwaltung von spezifischen Kraftübungen (ExerciseDetail)
 * und deren Sätzen (WorkoutSet) via RecyclerView.
 */
public class StrengthExerciseActivity extends AppCompatActivity {

    // Hält die Referenz auf das zu bearbeitende Krafttrainings-Objekt
    private StrengthExercise currentExercise;

    // Adapter zur Verwaltung der Übungslisten-Struktur
    private ExerciseDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_strength_exercise);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout_strength), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lade das Datenobjekt aus dem DataManager
        currentExercise = (StrengthExercise) DataManager.getInstance(this).getCurrentExerciseBeingEdited();

        // Initialisiere die UI mit den vorhandenen Daten
        EditText etName = findViewById(R.id.et_strength_name);
        if (currentExercise.getName() != null) {
            etName.setText(currentExercise.getName());
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView_exercise_details);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseDetailAdapter(currentExercise.getExerciseDetails());
        recyclerView.setAdapter(adapter);

        // Initialisiert die Logik zum manuellen Sortieren der Übungen
        setupDragAndDrop();

        // Floating Action Button, um dem Krafttraining eine neue Übung hinzuzufügen
        FloatingActionButton fab = findViewById(R.id.fab_add_exercise_detail);
        fab.setOnClickListener(v -> {
            currentExercise.addExerciseDetail("");
            adapter.notifyItemInserted(currentExercise.getExerciseDetails().size() - 1);
            recyclerView.scrollToPosition(currentExercise.getExerciseDetails().size() - 1);
            DataManager.getInstance(this).saveAllData(); // Speicherung
        });

        // Initialisierung der Session-übergreifenden Metriken
        EditText etNotes = findViewById(R.id.et_strength_notes);
        if (currentExercise.getNotes() != null) {
            etNotes.setText(currentExercise.getNotes());
        }
        if (currentExercise.getActualOverallRPE() > 0) {
            ((EditText) findViewById(R.id.et_strength_rpe)).setText(String.valueOf(currentExercise.getActualOverallRPE()));
        }

        // Konfiguration des Save-Verhaltens beim Verlassen der Activity
        MaterialToolbar toolbar = findViewById(R.id.toolbar_strength);
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
        // Allgemeine Text-Metriken aktualisieren
        EditText etName = findViewById(R.id.et_strength_name);
        currentExercise.setName(etName.getText().toString());

        EditText etNotes = findViewById(R.id.et_strength_notes);
        currentExercise.setNotes(etNotes.getText().toString());

        // RPE sicher konvertieren (Abfangen potenzieller Formatierungsfehler)
        EditText etRpe = findViewById(R.id.et_strength_rpe);
        String rpeText = etRpe.getText().toString();
        if (!rpeText.isEmpty()) {
            try {
                currentExercise.setActualOverallRPE(Float.parseFloat(rpeText));
            } catch (Exception e) {
                // Ignorieren der fehlerhaften Eingabe, RPE bleibt unverändert
            }
        }

        // Globales Speichern auslösen
        DataManager.getInstance(this).saveAllData();
        Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Implementiert die Logik für Drag & Drop, um dem Planer zu ermöglichen,
     * die Reihenfolge der Übungen (z. B. komplexe Grundübungen vor Isolationsübungen)
     * interaktiv anzupassen.
     */
    private void setupDragAndDrop() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_exercise_details);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {

                        int fromPosition = viewHolder.getBindingAdapterPosition();
                        int toPosition = target.getBindingAdapterPosition();

                        // Synchronisiert die visuelle UI-Verschiebung mit dem dahinterliegenden Datenmodell
                        Collections.swap(currentExercise.getExerciseDetails(), fromPosition, toPosition);
                        adapter.notifyItemMoved(fromPosition, toPosition);
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        // Swipe-to-Delete-Gesten sind deaktiviert,
                        // stattdessen wird ein expliziter Lösch-Button im UI verwendet.
                    }
                });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}