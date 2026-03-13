package com.example.trainingsapp.models;

/**
 * Abstrakte Basisklasse für alle Ausdauersportarten.
 * Erbt von ExerciseSession und bündelt Attribute, die alle Cardio-Einheiten gemeinsam haben.
 * * Die Klasse ist als 'abstract' deklariert, um die Erstellung generischer, undefinierter
 * Cardio-Objekte zu verhindern. Es müssen zwingend spezifische Subklassen
 * (z. B. RunCardio, SwimCardio) instanziiert werden.
 */
public abstract class CardioExercise extends ExerciseSession {

    // --- Trainingsphasen (USER STORY: Strukturierte Intervalle) ---
    // Ermöglicht dem Planer, detaillierte Anweisungen für den Ablauf zu geben.
    private String warmup = "";
    private String main = "";
    private String coolDown = "";

    // --- Geplante Zielwerte (Soll) ---
    private int targetDurationMin;
    private float targetDistanceKm;
    private int targetAverageHeartRate;
    private int targetMaxHeartRate;
    private float targetOverallRPE;

    // --- Tatsächlich erreichte Werte (Ist) ---
    private int actualDurationMin;
    private float actualDistanceKm;
    private int actualAverageHeartRate;
    private int actualMaxHeartRate;

    /**
     * Konstruktor der abstrakten Klasse.
     * Ruft den Konstruktor der Basisklasse (ExerciseSession) auf und setzt den
     * Enum-Typ explizit auf CARDIO.
     */
    public CardioExercise(String name) {
        super(name, ExerciseType.CARDIO);
    }

    // --- Getter und Setter für die Datenkapselung ---

    public String getWarmup() {
        return warmup;
    }

    public void setWarmup(String warmup) {
        this.warmup = warmup;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(String coolDown) {
        this.coolDown = coolDown;
    }

    public int getTargetDurationMin() {
        return targetDurationMin;
    }

    public void setTargetDurationMin(int targetDurationMin) {
        this.targetDurationMin = targetDurationMin;
    }

    public float getTargetDistanceKm() {
        return targetDistanceKm;
    }

    public void setTargetDistanceKm(float targetDistanceKm) {
        this.targetDistanceKm = targetDistanceKm;
    }

    public int getTargetAverageHeartRate() {
        return targetAverageHeartRate;
    }

    public void setTargetAverageHeartRate(int targetAverageHeartRate) {
        this.targetAverageHeartRate = targetAverageHeartRate;
    }

    public int getTargetMaxHeartRate() {
        return targetMaxHeartRate;
    }

    public void setTargetMaxHeartRate(int targetMaxHeartRate) {
        this.targetMaxHeartRate = targetMaxHeartRate;
    }

    public float getTargetOverallRPE() {
        return targetOverallRPE;
    }

    public void setTargetOverallRPE(float targetOverallRPE) {
        this.targetOverallRPE = targetOverallRPE;
    }

    public int getActualDurationMin() {
        return actualDurationMin;
    }

    public void setActualDurationMin(int actualDurationMin) {
        this.actualDurationMin = actualDurationMin;
    }

    public float getActualDistanceKm() {
        return actualDistanceKm;
    }

    public void setActualDistanceKm(float actualDistanceKm) {
        this.actualDistanceKm = actualDistanceKm;
    }

    public int getActualAverageHeartRate() {
        return actualAverageHeartRate;
    }

    public void setActualAverageHeartRate(int actualAverageHeartRate) {
        this.actualAverageHeartRate = actualAverageHeartRate;
    }

    public int getActualMaxHeartRate() {
        return actualMaxHeartRate;
    }

    public void setActualMaxHeartRate(int actualMaxHeartRate) {
        this.actualMaxHeartRate = actualMaxHeartRate;
    }
}