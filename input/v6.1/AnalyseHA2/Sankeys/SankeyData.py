import os
import plotly.graph_objects as go
import pandas as pd

# Pfad zur Datei im 'output'-Ordner relativ zum aktuellen Skript
script_dir = os.path.dirname(__file__)
base_file_path = 'C:/Users/xilef/Desktop/MATSim Local/matsim-berlin_aj/output/FINAL BASE/berlin-v6.1-bike-cluster-base-fixing-too-much-bike-400.output_trips.csv.gz'
target_file_path = 'C:/Users/xilef/Desktop/MATSim Local/matsim-berlin_aj/input/v6.1/AnalyseHA2/trips_with_person_info_updated_Wohnort_RSV_Cluster300_Hundekopf.csv'

# CSV-Dateien laden
csv1 = pd.read_csv(base_file_path, sep=';')
csv2 = pd.read_csv(target_file_path, sep=',')

#print(csv1.columns)
#print(csv2.columns)

# Verknüpfung der Daten anhand der "person"-ID
df = pd.DataFrame({
    'person_id': csv1['person'],       # First column from first CSV
    'main_mode_1': csv1['main_mode'],  # Second column from first CSV
    'main_mode_2': csv2['main_mode'],   # Second column from second CSV
    'region': csv2['RingOderAussen']
})

# Ergebnis speichern
output_file_path = os.path.join(script_dir, 'RSV_Sankey_CSV')
df.to_csv(output_file_path, index=False)

print("CSV-Datei erfolgreich verknüpft und gespeichert als 'RSV_Sankey_CSV...'")

# Get a unique list of labels (nodes)
all_labels = df.iloc[:, 1].tolist()

# Convert labels to indices for Plotly
source_indices = [all_labels.index(src) for src in all_labels]
target_indices = [all_labels.index(tgt) for tgt in all_labels]

# Set the same value for all links (you mentioned each link has equal value)
values = [1/len(df)] * len(df)  # Divide value equally if there are many links

# Create the Sankey diagram
fig = go.Figure(go.Sankey(
    node=dict(
        pad=15,
        thickness=20,
        line=dict(color="black", width=0.5),
        label=all_labels
    ),
    link=dict(
        source=source_indices,  # Indices of the source nodes
        target=target_indices,  # Indices of the target nodes
        value=values            # Equal values for each link
    )
))

# Update layout for better visuals
fig.update_layout(title_text="Sankey Diagram", font_size=10)

# Show the Sankey diagram
fig.show()
