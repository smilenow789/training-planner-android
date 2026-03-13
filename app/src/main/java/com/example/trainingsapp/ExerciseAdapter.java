package com.example.trainingsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainingsapp.models.ExerciseSession;
import com.example.trainingsapp.models.ExerciseStatus;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Adapter-Klasse zur Anbindung der Liste von Trainingseinheiten (ExerciseSession)
 * an die RecyclerView der Tagesübersicht (DayActivity).
 * Verwaltet die Darstellung, den Status-Workflow und die Navigation in die Detail-Masken.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    // Die darzustellende Datenquelle, bestehend aus Instanzen der abstrakten Basisklasse ExerciseSession
    private List<ExerciseSession> exerciseList;

    /**
     * Konstruktor zur Initialisierung des Adapters mit der entsprechenden Datenquelle.
     *
     * @param exerciseList Die Liste der Trainingseinheiten eines Tages.
     */
    public ExerciseAdapter(List<ExerciseSession> exerciseList) {
        this.exerciseList = exerciseList;
    }

    /**
     * Wird vom LayoutManager aufgerufen, um ein neues ViewHolder-Objekt
     * zu generieren, falls keine recycelten Views zur Wiederverwendung bereitstehen.
     */
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate wandelt die XML-Definition der Karte in ein View-Objekt um
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    /**
     * Verbindet die tatsächlichen Daten des Modells an einer bestimmten Listenposition
     * mit den entsprechenden UI-Elementen des ViewHolders.
     */
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseSession currentExercise = exerciseList.get(position);
        holder.tvName.setText(currentExercise.getName());

        Context context = holder.itemView.getContext();

        // Abrufen der kontextabhängigen Material Design 3 Farb-Tokens.
        // Gewährleistet eine korrekte Darstellung im Light- und Dark-Mode.
        int colorSurfaceVariant = com.google.android.material.color.MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorSurfaceVariant);
        int colorSecondaryContainer = com.google.android.material.color.MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorSecondaryContainer);
        int colorPrimaryContainer = com.google.android.material.color.MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorPrimaryContainer);
        int colorErrorContainer = com.google.android.material.color.MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorErrorContainer);

        // Logische Zuweisung der Farb-Tokens basierend auf dem aktuellen Status der Trainings-Einheit
        switch (currentExercise.getExerciseStatus()) {
            case PLANNED:
                holder.btnStatus.setText(R.string.status_planned);
                holder.btnStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorSurfaceVariant));
                break;
            case ACTIVE:
                holder.btnStatus.setText(R.string.status_active);
                holder.btnStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorSecondaryContainer));
                break;
            case COMPLETED:
                holder.btnStatus.setText(R.string.status_completed);
                holder.btnStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorPrimaryContainer));
                break;
            case SKIPPED:
                holder.btnStatus.setText(R.string.status_skipped);
                holder.btnStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorErrorContainer));
                break;
        }

        // Definition des Click-Listeners für den Status-Button zur Öffnung des Auswahl-Dialogs
        holder.btnStatus.setOnClickListener(v -> {
            String[] statuses = {
                    context.getString(R.string.status_planned),
                    context.getString(R.string.status_active),
                    context.getString(R.string.status_completed),
                    context.getString(R.string.status_skipped)
            };

            new com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.choose_status)
                    .setItems(statuses, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                currentExercise.setExerciseStatus(ExerciseStatus.PLANNED);
                                break;
                            case 1:
                                currentExercise.setExerciseStatus(ExerciseStatus.ACTIVE);
                                break;
                            case 2:
                                currentExercise.setExerciseStatus(ExerciseStatus.COMPLETED);
                                break;
                            case 3:
                                currentExercise.setExerciseStatus(ExerciseStatus.SKIPPED);
                                break;
                        }
                        DataManager.getInstance(context).saveAllData(); // Persistentes Speichern der Status-Änderung
                        notifyItemChanged(holder.getBindingAdapterPosition()); // UI lokal aktualisieren
                    }).show();
        });

        // Definition des Click-Listeners auf Kartenebene zur Navigation in die Detailansicht
        // Nutzt instanceof, um das korrekte Ziel (Activity) aufzulösen.
        holder.itemView.setOnClickListener(v -> {
            // Speichert die Referenz auf das Objekt für die Folge-Activity im DataManager-Singleton
            DataManager.getInstance(context).setCurrentExerciseBeingEdited(currentExercise);

            if (currentExercise instanceof com.example.trainingsapp.models.StrengthExercise) {
                context.startActivity(new android.content.Intent(context, StrengthExerciseActivity.class));
            } else if (currentExercise instanceof com.example.trainingsapp.models.CardioExercise) {
                context.startActivity(new android.content.Intent(context, CardioExerciseActivity.class));
            } else {
                context.startActivity(new android.content.Intent(context, CycleBallExerciseActivity.class));
            }
        });

        // Definition der Lösch-Routine eines Trainings
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) { // Zur Vermeidung von ArrayIndexOutOfBoundsExceptions
                exerciseList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, exerciseList.size());
                DataManager.getInstance(context).saveAllData(); // Löschung persistent speichern
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    /**
     * Hilfsmethode, um dem RecyclerView mitzuteilen, dass am Ende der Liste ein
     * neues Element hinzugefügt wurde.
     */
    public void notifyItemAdded() {
        notifyItemInserted(exerciseList.size() - 1);
    }

    /**
     * Der ViewHolder dient als interner Cache für die UI-Komponenten eines Listenelements.
     * Er verhindert kostspielige und redundante 'findViewById()'-Aufrufe während des Scrollens.
     */
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        MaterialButton btnStatus;
        MaterialButton btnDelete;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_exercise_name);
            btnStatus = itemView.findViewById(R.id.btn_status);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}