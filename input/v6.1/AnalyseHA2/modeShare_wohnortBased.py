import os
import pandas as pd
import matplotlib.pyplot as plt

# Pfad zur Datei im aktuellen Verzeichnis des Skripts
script_dir = os.path.dirname(__file__)
output_file_path = os.path.join(script_dir, 'trips_with_person_info_updated_Wohnort_BaseCase_Cluster400_neu.csv')

# CSV-Datei laden
trips_df = pd.read_csv(output_file_path)

# Berechnung des Anteils jedes 'main_mode' für jede 'RingOderAussen'-Gruppe
mode_distribution = trips_df.groupby(['RingOderAussen', 'main_mode']).size().unstack(fill_value=0)

# Berechnung der Prozentsätze
mode_distribution_percent = mode_distribution.div(mode_distribution.sum(axis=1), axis=0) * 100

# Speichern der berechneten Prozentsätze als CSV-Datei
result_file_path = os.path.join(script_dir, 'Wohnort_main_mode_distribution_BaseCase_Cluster400.csv')
mode_distribution_percent.to_csv(result_file_path)

print(f"Tabelle mit dem Anteil jedes 'main_mode' in jeder 'RingOderAussen'-Gruppe erfolgreich gespeichert als '{result_file_path}'")

# Diagramm erstellen
ax = mode_distribution_percent.plot(kind='bar', stacked=True, figsize=(12, 8), colormap='tab20')
plt.title('Anteil der Hauptverkehrsmittel pro Wohnort-Region')
plt.xlabel('Wohnort')
plt.ylabel('Anteil (%)')
plt.legend(title='Hauptverkehrsmittel', bbox_to_anchor=(1.05, 1), loc='upper left')
plt.tight_layout()

# Prozentzahlen als Beschriftungen auf den Balken
for container in ax.containers:
    ax.bar_label(container, fmt='%.1f%%', label_type='center')

# Diagramm speichern
chart_file_path = os.path.join(script_dir, 'Wohnort_main_mode_distribution_BaseCase_Cluster400.png')
plt.savefig(chart_file_path)

print(f"Diagramm erfolgreich gespeichert als '{chart_file_path}'")
