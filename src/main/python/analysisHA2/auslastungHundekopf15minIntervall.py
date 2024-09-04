import gzip
import pandas as pd
import geopandas as gpd
from shapely.geometry import Point
import xml.etree.ElementTree as ET

# Pfad zum Hundekopf-Shapefile
hundekopf_shapefile = r'C:\Users\jamps\MatSim\BerlinHA2\input\v6.1\hundekopf\hundekopf25832eineForm.shp'
hundekopf_gdf = gpd.read_file(hundekopf_shapefile)

# Wandle die CRS des Hundekopf Shapefiles falls notwendig
hundekopf_gdf = hundekopf_gdf.to_crs(epsg=25832)

# Pfad zur Events XML-Datei
events_file = r'C:\Users\jamps\MatSim\BerlinHA2\output\berlin-v6.1-0.1pct\2Iters\berlin-v6.1.output_events.xml.gz'

# Entpacken der .gz-Datei
with gzip.open(events_file, 'rb') as f:
    tree = ET.parse(f)

# Extrahiere die Wurzel des Baums
root = tree.getroot()

# Parsen der Events Datei
events = []

# Extrahieren von Fahrzeugbewegungs-Events
for event in root.iter('event'):
    if event.attrib['type'] == 'vehicle enters link':
        if 'x' in event.attrib and 'y' in event.attrib:
            events.append({
                'time': float(event.attrib['time']),
                'x': float(event.attrib['x']),
                'y': float(event.attrib['y']),
                'event_type': event.attrib['type']
            })

events_df = pd.DataFrame(events)

# Wandle die Koordinaten in Points um
events_df['geometry'] = events_df.apply(lambda row: Point(row['x'], row['y']), axis=1)
events_gdf = gpd.GeoDataFrame(events_df, geometry='geometry', crs="EPSG:25832")

# Finde die Events innerhalb des Hundekopfs
hundekopf_events_gdf = gpd.sjoin(events_gdf, hundekopf_gdf, how='inner', predicate='within')

# Filtere die Fahrzeugbewegungs-Events
vehicle_movements_df = hundekopf_events_gdf[hundekopf_events_gdf['event_type'] == 'vehicle enters link']

# Konvertiere die Zeit in 15-Minuten-Intervalle
vehicle_movements_df['time_interval'] = vehicle_movements_df['time'].apply(lambda t: (int(t) // 900) * 900)

# Zähle die Anzahl der Fahrzeuge pro 15 Minuten Intervalle
vehicle_counts = vehicle_movements_df.groupby('time_interval').size().reset_index(name='vehicle_count')

# Speichere das Ergebnis als CSV
output_file = 'vehicle_counts_hundekopf.csv'
vehicle_counts.to_csv(output_file, index=False)

print(f"Ergebnis gespeichert in {output_file}")
