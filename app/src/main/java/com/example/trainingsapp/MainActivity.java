package com.example.trainingsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trainingsapp.models.Day;
import com.example.trainingsapp.models.ExerciseSession;
import com.example.trainingsapp.models.ExerciseStatus;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Locale;

/**
 * Die Haupt-Activity (Einstiegspunkt) der Applikation.
 * Fungiert als Dashboard und stellt eine wochenbasierte Übersicht (Montag bis Sonntag) dar.
 * Beinhaltet Kernfunktionen wie die Navigation zwischen Kalenderwochen und das
 * intelligente Kopieren von Trainingsplänen aus der Vorwoche.
 */
public class MainActivity extends AppCompatActivity {

    // Referenzen auf die statischen UI-Container der 7 Wochentage.
    private final int[] dayLayoutIds = {
            R.id.day_mo, R.id.day_di, R.id.day_mi, R.id.day_do,
            R.id.day_fr, R.id.day_sa, R.id.day_so
    };

    // Hält das logische "Anker-Datum" (immer ein Montag) der aktuell angezeigten Woche
    private LocalDate currentWeekMonday;

    // Referenz auf den Singleton-DataManager für den Datenzugriff
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dataManager = DataManager.getInstance(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Sucht iterativ den Montag der aktuellen Systemwoche
        LocalDate today = LocalDate.now();
        currentWeekMonday = today.minusDays(today.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        // Eine Woche in die Vergangenheit springen
        topAppBar.setNavigationOnClickListener(v -> {
            currentWeekMonday = currentWeekMonday.minusWeeks(1);
            updateUI();
        });

        // Eine Woche in die Zukunft springen
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.next_week) {
                currentWeekMonday = currentWeekMonday.plusWeeks(1);
                updateUI();
                return true;
            }
            return false;
        });
    }

    /**
     * Android Lifecycle-Methode.
     * Wird aufgerufen, sobald die Activity wieder in den Vordergrund rückt.
     * Erzwingt einen UI-Refresh, um eventuelle Status-Änderungen (z.B. Training "Erledigt"),
     * die in der DayActivity gemacht wurden, im Fortschrittskreis darzustellen.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * Zur Synchronisation der UI mit dem aktuellen Datenmodell.
     * Berechnet die Kalenderwoche, rendert die 7 Tages-Container und prüft, ob die
     * intelligente Kopier-Funktion für Vorwochen angeboten werden soll.
     */
    private void updateUI() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        // ISO-Standard zur korrekten Ermittlung der Kalenderwoche
        int kw = currentWeekMonday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year = currentWeekMonday.getYear();
        topAppBar.setTitle(getString(R.string.kw_format, kw, year));

        boolean currentWeekIsEmpty = true;
        boolean lastWeekHasData = false;

        // Validierung: Enthält die vorherige Woche planbare Trainingsdaten?
        for (int i = 0; i < 7; i++) {
            String lastWeekDateStr = currentWeekMonday.minusWeeks(1).plusDays(i).toString();
            Day lastWeekDay = dataManager.getDay(lastWeekDateStr);
            if (!lastWeekDay.getExercises().isEmpty()) {
                lastWeekHasData = true;
                break;
            }
        }

        // Iteratives Rendern der 7 Wochentage der aktuell ausgewählten Woche
        for (int i = 0; i < 7; i++) {
            LocalDate dateToRender = currentWeekMonday.plusDays(i);
            String dateStr = dateToRender.toString();
            Day dayData = dataManager.getDay(dateStr);

            if (!dayData.getExercises().isEmpty()) {
                currentWeekIsEmpty = false;
            }

            View dayLayout = findViewById(dayLayoutIds[i]);
            setupDayView(dayLayout, dateToRender, dayData);
        }

        // Bietet dem Planer an, die leere Woche mit Daten der Vorwoche zu füllen
        if (currentWeekIsEmpty && lastWeekHasData) {
            askToCopyLastWeek();
        }
    }

    /**
     * Erzeugt einen Dialog, der den Kopiervorgang verifiziert.
     */
    private void askToCopyLastWeek() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_copy_week_title)
                .setMessage(R.string.dialog_copy_week_msg)
                .setPositiveButton(R.string.btn_copy, (dialog, which) -> copyLastWeek())
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    /**
     * Führt eine tiefe Kopie der Trainingsdaten von der Vorwoche in die aktuelle Woche aus.
     * Nutzt die Methode copyExerciseAsPlanned des DataManagers, um Ist-Werte zu nullen
     * und den Status auf PLANNED zurückzusetzen.
     */
    private void copyLastWeek() {
        try {
            for (int i = 0; i < 7; i++) {
                String lastWeekDateStr = currentWeekMonday.minusWeeks(1).plusDays(i).toString();
                String thisWeekDateStr = currentWeekMonday.plusDays(i).toString();

                Day lastWeekDay = dataManager.getDay(lastWeekDateStr);
                Day thisWeekDay = dataManager.getDay(thisWeekDateStr);

                // Iteriert über alle Einheiten des Vorwochen-Tages und klont diese
                for (ExerciseSession ex : lastWeekDay.getExercises()) {
                    ExerciseSession copiedEx = dataManager.copyExerciseAsPlanned(ex);
                    thisWeekDay.getExercises().add(copiedEx);
                }
            }
            dataManager.saveAllData(); // Persistiert die neu geklonte Woche
            updateUI(); // Rendert die neu hinzugefügten Daten auf dem Bildschirm
            Toast.makeText(this, R.string.msg_week_copied, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.msg_week_copy_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Konfiguriert die Darstellung eines einzelnen Tages-Containers in der Wochenübersicht.
     * Berechnet den Progress und richtet den Navigations-Intent ein.
     *
     * @param dayLayout Die Referenz auf das spezifische UI-Element des Tages
     * @param date      Das exakte LocalDate-Objekt des Tages
     * @param dayData   Das zugehörige Datenmodell aus dem DataManager
     */
    private void setupDayView(View dayLayout, LocalDate date, Day dayData) {
        TextView tvDate = dayLayout.findViewById(R.id.tv_day_date);

        // Formatierung des Datums für die UI (z. B. "Mo, 09.03.")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd.MM.", Locale.GERMAN);
        tvDate.setText(date.format(formatter));

        com.google.android.material.progressindicator.CircularProgressIndicator progressCircle =
                dayLayout.findViewById(R.id.progress_day_status);

        // Berechnung des täglichen Fortschritts
        int totalExercises = dayData.getExercises().size();

        if (totalExercises == 0) {
            // Ausblenden des Indikators, falls es sich um einen Ruhetag ohne Trainings handelt
            progressCircle.setVisibility(View.INVISIBLE);
        } else {
            progressCircle.setVisibility(View.VISIBLE);
            int doneCount = 0;
            // Zählt alle Einheiten, deren Status einen Abschluss des Workflows signalisiert
            for (ExerciseSession ex : dayData.getExercises()) {
                if (ex.getExerciseStatus() == ExerciseStatus.COMPLETED || ex.getExerciseStatus() == ExerciseStatus.SKIPPED) {
                    doneCount++;
                }
            }
            // Berechnung des prozentualen Fortschritts
            int progressValue = (doneCount * 100) / totalExercises;
            progressCircle.setProgressCompat(progressValue, true);
        }

        // Listener zur Erstellung eines Intents zur DayActivity
        dayLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, DayActivity.class);
            // Übergabe des Datums als String
            intent.putExtra("EXTRA_DATE", date.toString());
            startActivity(intent);
        });
    }
}