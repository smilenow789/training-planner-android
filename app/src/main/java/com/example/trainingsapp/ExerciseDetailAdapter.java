package com.example.trainingsapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainingsapp.models.ExerciseDetail;
import com.example.trainingsapp.models.WorkoutSet;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Adapter zur Darstellung der Krafttraining-Benutzeroberfläche.
 * Bindet eine Liste von Übungen (ExerciseDetail) an die RecyclerView und generiert innerhalb
 * jeder Übung dynamisch eine untergeordnete Tabellenansicht für Trainingssätze (WorkoutSet).
 */
public class ExerciseDetailAdapter extends RecyclerView.Adapter<ExerciseDetailAdapter.DetailViewHolder> {

    // Liste der einzelnen Kraftübungen (z.B. Bankdrücken, Kniebeugen)
    private List<ExerciseDetail> detailsList;

    public ExerciseDetailAdapter(List<ExerciseDetail> detailsList) {
        this.detailsList = detailsList;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_detail, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        ExerciseDetail currentDetail = detailsList.get(position);

        // Anzeige des festgelegten Übungsnamens
        holder.etName.setText(currentDetail.getExerciseName());

        // Anzeige der vom Trainer geplanten Anzahl an Sätzen.
        // 0 werden ausgeblendet, um die User Experience (Tipp-Interaktion) zu verbessern.
        if (currentDetail.getTargetSetCount() > 0) {
            holder.etTargetSets.setText(String.valueOf(currentDetail.getTargetSetCount()));
        } else {
            holder.etTargetSets.setText("");
        }

        // Name in das Model sichern, sobald der Nutzer tippt
        holder.etName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentDetail.setExerciseName(s.toString());
            }
        });

        // Gewünschte Satzanzahl in das Model sichern
        holder.etTargetSets.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    currentDetail.setTargetSetCount(Integer.parseInt(s.toString()));
                } else {
                    currentDetail.setTargetSetCount(0);
                }
            }
        });

        // Lösch-Logik für einen gesamten Übungsblock (inkl. aller Sätze)
        holder.btnDeleteExercise.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                detailsList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, detailsList.size());
            }
        });

        // Initialisierung der verschachtelten Tabellenstruktur
        drawSets(holder, currentDetail);

        // Athlet entscheidet sich für einen zusätzlichen Satz
        holder.btnAddSet.setOnClickListener(v -> {
            currentDetail.addWorkoutSet(new WorkoutSet(0, 0, 0));
            drawSets(holder, currentDetail); // Erzwingt einen Re-Draw der verschachtelten UI
        });

        // Athlet streicht den letzten Satz
        holder.btnRemoveSet.setOnClickListener(v -> {
            currentDetail.removeWorkoutSet();
            drawSets(holder, currentDetail); // Erzwingt einen Re-Draw der verschachtelten UI
        });
    }

    /**
     * Generiert und rendert die tabellarische Übersicht der Trainingssätze dynamisch.
     *
     * @param holder Der Container der übergeordneten Kraftübung
     * @param detail Das Datenmodell der übergeordneten Kraftübung
     */
    private void drawSets(DetailViewHolder holder, ExerciseDetail detail) {
        // Container bereinigen
        holder.llSetsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

        for (int i = 0; i < detail.getSets().size(); i++) {
            WorkoutSet currentSet = detail.getSets().get(i);

            // Konvertiert das XML Layout in eine View und fügt sie dem Container hinzu
            View setView = inflater.inflate(R.layout.item_workout_set, holder.llSetsContainer, false);

            EditText etTargetWeight = setView.findViewById(R.id.et_target_weight);
            EditText etTargetReps = setView.findViewById(R.id.et_target_reps);
            EditText etTargetRir = setView.findViewById(R.id.et_target_rir);
            EditText etActualWeight = setView.findViewById(R.id.et_actual_weight);
            EditText etActualReps = setView.findViewById(R.id.et_actual_reps);
            EditText etActualRir = setView.findViewById(R.id.et_actual_rir);

            // Übertragung bestehender Daten in die Views (Soll-Werte)
            if (currentSet.getTargetWeight() > 0)
                etTargetWeight.setText(String.valueOf(currentSet.getTargetWeight()));
            if (currentSet.getTargetReps() > 0)
                etTargetReps.setText(String.valueOf(currentSet.getTargetReps()));
            if (currentSet.getTargetRir() > 0)
                etTargetRir.setText(String.valueOf(currentSet.getTargetRir()));

            // Übertragung bestehender Daten in die Views (Ist-Werte)
            if (currentSet.getActualWeight() > 0)
                etActualWeight.setText(String.valueOf(currentSet.getActualWeight()));
            if (currentSet.getActualReps() > 0)
                etActualReps.setText(String.valueOf(currentSet.getActualReps()));
            if (currentSet.getActualRir() > 0)
                etActualRir.setText(String.valueOf(currentSet.getActualRir()));

            // Eingegebene Ziel-Werte im Model speichern
            etTargetWeight.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) currentSet.setTargetWeight(Float.parseFloat(s.toString()));
                }
            });
            etTargetReps.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) currentSet.setTargetReps(Integer.parseInt(s.toString()));
                }
            });
            etTargetRir.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) currentSet.setTargetRir(Float.parseFloat(s.toString()));
                }
            });

            // Eingegebene Ist-Werte im Model speichern
            etActualWeight.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) currentSet.setActualWeight(Float.parseFloat(s.toString()));
                }
            });
            etActualReps.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) currentSet.setActualReps(Integer.parseInt(s.toString()));
                }
            });
            etActualRir.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) currentSet.setActualRir(Float.parseFloat(s.toString()));
                }
            });

            // Die initialisierte Tabellenzeile dem Container hinzufügen
            holder.llSetsContainer.addView(setView);
        }
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    /**
     * Container-Klasse (ViewHolder) für eine einzelne Übung
     */
    static class DetailViewHolder extends RecyclerView.ViewHolder {
        EditText etName, etTargetSets;
        MaterialButton btnAddSet, btnRemoveSet, btnDeleteExercise;
        LinearLayout llSetsContainer;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.et_detail_name);
            etTargetSets = itemView.findViewById(R.id.et_target_sets);
            btnAddSet = itemView.findViewById(R.id.btn_add_set);
            btnRemoveSet = itemView.findViewById(R.id.btn_remove_set);
            btnDeleteExercise = itemView.findViewById(R.id.btn_delete_exercise);
            llSetsContainer = itemView.findViewById(R.id.ll_sets_container);
        }
    }

    /**
     * Abstrakte Hilfsklasse zur Reduzierung von Code beim Implementieren
     * Zwingt abgeleitete Instanzen nur zur Implementierung der relevanten Methode (onTextChanged).
     */
    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}