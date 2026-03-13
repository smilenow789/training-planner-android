# TrainingsApp

Eine Android-Applikation zur hybriden Trainingsplanung und -erfassung (Kraft, Ausdauer, Radball).

## Überblick
Die TrainingsApp ermöglicht es Athleten und Trainern, komplexe Trainingswochen zu planen und die tatsächliche Durchführung zu tracken. Besonderer Fokus liegt auf der Unterstützung verschiedener Sportarten (Hybrid-Ansatz) und der einfachen Übernahme von Plänen aus Vorwochen.

## Kernfunktionen
- **Wochenbasierte Übersicht:** Dashboard zur Navigation durch Kalenderwochen.
- **Hybrides Training:** Unterstützung für Krafttraining, Cardio (Laufen, Schwimmen, Radfahren) und Radball.
- **Intelligente Kopierfunktion:** Komplette Trainingswochen können als "geplant" in die neue Woche kopiert werden, wobei die Ist-Werte zurückgesetzt werden.
- **Detailtiefe:** Erfassung von Sätzen (Gewicht, Wiederholungen, RIR) im Kraftsport sowie Pace, Puls und RPE im Ausdauersport.
- **Drag & Drop:** Interaktive Sortierung von Übungen und Trainings zur optimalen Belastungssteuerung.
- **Persistenz:** Lokale Datenspeicherung via JSON (SharedPreferences).

## Technische Details
- **Plattform:** Android (minSdk 34)
- **Sprache:** Java
- **UI:** Material Design 3
- **Architektur:** MVC-Ansatz mit Singleton-DataManager
- **Bibliotheken:** Gson, JUnit, Espresso