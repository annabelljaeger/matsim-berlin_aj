import os
import pandas as pd

# Pfad zur Datei im 'output'-Ordner relativ zum aktuellen Skript
script_dir = os.path.dirname(__file__)
trips_file_path = os.path.join(script_dir, 'C:/Users/jamps/MatSim/HA2_Zusatzdateien/berlin-v6.1-maut-400.300.trips.csv.gz')
#"C:\Users\jamps\MatSim\Hausaufgabe2\output\berlin-v6.1-0.1pct\2Iters\berlin-v6.1.output_trips.csv.gz"
persons_file_path = os.path.join(script_dir, 'C:/Users/jamps/MatSim/BerlinHA2/output/outputsFuerAnalyse/berlin-v6.1-bike-cluster-base-fixing-too-much-bike-400.output_persons.csv.gz')

# CSV-Dateien laden
trips_df = pd.read_csv(trips_file_path, sep=';')
persons_df = pd.read_csv(persons_file_path, sep=';')

# Verknüpfung der Daten anhand der "person"-ID
merged_df = pd.merge(trips_df, persons_df[['person', 'income', 'home_x', 'home_y']], on='person', how='left')

# Ergebnis speichern
output_file_path = os.path.join(script_dir, 'trips_with_person_info_Maut_ClusterUnfertig300.csv.gz')
merged_df.to_csv(output_file_path, index=False)

print("CSV-Datei erfolgreich verknüpft und gespeichert als 'trips_with_person_info...'")


