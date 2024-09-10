import os
import pandas as pd
import matplotlib.pyplot as plt

# Pfad zur Datei im aktuellen Verzeichnis des Skripts
script_dir = os.path.dirname(__file__)
output_file_path = os.path.join(script_dir, 'trips_with_person_info_BaseCase_Cluster400.csv.gz')

# CSV-Datei laden
trips_df = pd.read_csv(output_file_path)

# Berechnung des Anteils jedes 'main_mode' für jede 'income'-Gruppe
mode_distribution = trips_df.groupby(['income', 'main_mode']).size().unstack(fill_value=0)

# Berechnung der Prozentsätze
mode_distribution_percent = mode_distribution.div(mode_distribution.sum(axis=1), axis=0) * 100

# Ergebnis speichern
result_file_path = os.path.join(script_dir, 'income_main_mode_distribution_BaseCase_Cluster400.csv')
mode_distribution_percent.to_csv(result_file_path)

print("Tabelle mit dem Anteil jedes 'main_mode' in jeder 'income'-Gruppe erfolgreich gespeichert als 'income_main_mode_distribution.csv'")

# Diagramm erstellen
ax = mode_distribution_percent.plot(kind='bar', stacked=True, figsize=(12, 8), colormap='tab20')
plt.title('Anteil der Hauptverkehrsmittel pro Einkommensgruppe')
plt.xlabel('Einkommensgruppe')
plt.ylabel('Anteil (%)')
plt.legend(title='Hauptverkehrsmittel', bbox_to_anchor=(1.05, 1), loc='upper left')
plt.tight_layout()

# Diagramm speichern
chart_file_path = os.path.join(script_dir, 'income_main_mode_distribution_BaseCase_Cluster400.png')
plt.savefig(chart_file_path)

print("Diagramm erfolgreich gespeichert als 'income_main_mode_distribution.png'")

