package com.example.trainingsapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainingsapp.models.Day;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Activity zur Darstellung und Verwaltung eines einzelnen Trainingstages.
 * Agiert als Container, um verschiedene Trainingseinheiten in einer
 * gemeinsamen Liste (RecyclerView) darzustellen und zu ordnen (Drag & Drop).
 */
public class DayActivity extends AppCompatActivity {

    private Day currentDay;
    private String currentDateString;
    private ExerciseAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_day);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout_day), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Intent-Daten empfangen und das entsprechende Day-Objekt aus dem Manager laden
        currentDateString = getIntent().getStringExtra("EXTRA_DATE");
        if (currentDateString == null) currentDateString = LocalDate.now().toString(); // Fallback
        currentDay = DataManager.getInstance(this).getDay(currentDateString);

        setupToolbar();
        setupRecyclerView();
        setupDragAndDrop();
        setupFloatingActionButton();

        // Existierende Tages-Notizen in das UI laden
        EditText etNotes = findViewById(R.id.et_day_notes);
        if (currentDay.getNotes() != null) {
            etNotes.setText(currentDay.getNotes());
        }
    }

    /**
     * Stellt sicher, dass die Liste neu gezeichnet wird, falls der Nutzer
     * in der Detail-Ansicht den Namen oder Status einer Einheit geändert hat.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Löst eine Datensicherung aus, sobald die Activity in den Hintergrund rückt
     * oder verlassen wird. Verhindert die Erstellung leerer Daten-Einträge.
     */
    @Override
    protected void onPause() {
        super.onPause();
        EditText etNotes = findViewById(R.id.et_day_notes);
        currentDay.setNotes(etNotes.getText().toString());

        // Ressourcenschonung: Ein Tag wird nur persistent gespeichert,
        // wenn tatsächlich Übungen oder Notizen vorhanden sind.
        if (!currentDay.getExercises().isEmpty() || !currentDay.getNotes().isEmpty()) {
            DataManager.getInstance(this).saveAllData();
        }
    }

    /**
     * Konfiguriert die obere App-Leiste (Toolbar) und formatiert das Datum leserlich.
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_day);
        try {
            LocalDate date = LocalDate.parse(currentDateString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", Locale.GERMAN);
            toolbar.setTitle(date.format(formatter));
        } catch (Exception e) {
            toolbar.setTitle(currentDateString);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Initialisiert den RecyclerView mit dem entsprechenden Adapter für die Liste.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_exercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(currentDay.getExercises());
        recyclerView.setAdapter(adapter);
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab_add_exercise);
        fab.setOnClickListener(v -> showAddExerciseDialog());
    }

    /**
     * Zeigt einen Auswahl-Dialog an, um dynamisch verschiedene Subklassen von
     * ExerciseSession zu instanziieren und der Liste hinzuzufügen.
     */
    private void showAddExerciseDialog() {
        String[] exerciseTypes = {
                getString(R.string.exercise_type_strength),
                getString(R.string.exercise_type_swimming),
                getString(R.string.exercise_type_running),
                getString(R.string.exercise_type_cycling),
                getString(R.string.exercise_type_cycleball)
        };

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_add_exercise_title))
                .setItems(exerciseTypes, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            currentDay.addStrengthExercise(getString(R.string.exercise_type_strength));
                            break;
                        case 1:
                            currentDay.addSwimCardio(getString(R.string.exercise_type_swimming));
                            break;
                        case 2:
                            currentDay.addRunCardio(getString(R.string.exercise_type_running));
                            break;
                        case 3:
                            currentDay.addBicycleCardio(getString(R.string.exercise_type_cycling));
                            break;
                        case 4:
                            currentDay.addCycleBallExercise(getString(R.string.exercise_type_cycleball));
                            break;
                    }
                    adapter.notifyItemAdded();
                    DataManager.getInstance(this).saveAllData();
                }).show();
    }

    /**
     * Konfiguriert die ItemTouchHelper-Klasse, um Drag & Drop Funktionalität für
     * das Sortieren der Trainingseinheiten zu ermöglichen.
     */
    private void setupDragAndDrop() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_exercises);
        androidx.recyclerview.widget.ItemTouchHelper itemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(
                new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(
                        androidx.recyclerview.widget.ItemTouchHelper.UP | androidx.recyclerview.widget.ItemTouchHelper.DOWN, 0) {

                    @Override
                    public boolean onMove(@androidx.annotation.NonNull RecyclerView recyclerView,
                                          @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder,
                                          @androidx.annotation.NonNull RecyclerView.ViewHolder target) {
                        int fromPosition = viewHolder.getBindingAdapterPosition();
                        int toPosition = target.getBindingAdapterPosition();

                        // Tauscht die logische Position im Modell aus
                        java.util.Collections.swap(currentDay.getExercises(), fromPosition, toPosition);
                        // Informiert den Adapter über die visuelle Änderung
                        adapter.notifyItemMoved(fromPosition, toPosition);
                        return true;
                    }

                    @Override
                    public void onSwiped(@androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        // Swipe-to-Delete ist hier deaktiviert
                    }

                    /**
                     * Wird aufgerufen, sobald der Nutzer das Item nach dem Verschieben loslässt.
                     * Hier wird der neue Zustand persistent gespeichert.
                     */
                    @Override
                    public void clearView(@androidx.annotation.NonNull RecyclerView recyclerView, @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder) {
                        super.clearView(recyclerView, viewHolder);
                        DataManager.getInstance(DayActivity.this).saveAllData();
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}