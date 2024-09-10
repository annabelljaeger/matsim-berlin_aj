import pandas as pd
import geopandas as gpd
from shapely.geometry import Point
import os

# Dateipfade
script_dir = os.path.dirname(__file__)
trips_file_path = os.path.join(script_dir, 'trips_with_person_info_Maut_ClusterUnfertig300.csv.gz')
hundekopf_shapefile_path = os.path.join(script_dir, 'hundekopf', 'hundekopfNeu.shp')
aussenbezirke_shapefile_path = os.path.join(script_dir, 'aussenbezirke', 'aussenbezirke2.shp')

# CSV-Datei und Shapefiles laden
trips_df = pd.read_csv(trips_file_path)
hundekopf_gdf = gpd.read_file(hundekopf_shapefile_path).to_crs(epsg=25832)
aussenbezirke_gdf = gpd.read_file(aussenbezirke_shapefile_path).to_crs(epsg=25832)

# Debug-Ausgaben: CRS überprüfen
print("Hundekopf CRS:", hundekopf_gdf.crs)
print("Außenbezirke CRS:", aussenbezirke_gdf.crs)
print("Trips CRS: EPSG:25832")

# Geodatenframe für Trips erstellen
trips_gdf = gpd.GeoDataFrame(
    trips_df,
    geometry=gpd.points_from_xy(trips_df['home_x'], trips_df['home_y']),
    crs='EPSG:25832'
)

# Debug-Ausgaben: Überprüfen der ersten paar Zeilen der GeoDataFrames
print("Beispiel Trips GeoDataFrame:")
print(trips_gdf.head())
print("Beispiel Hundekopf GeoDataFrame:")
print(hundekopf_gdf.head())
print("Beispiel Außenbezirke GeoDataFrame:")
print(aussenbezirke_gdf.head())

# Zuweisung der Trips zu den Gebieten
trips_with_area = trips_gdf.copy()
trips_with_area['RingOderAussen'] = 'Sonstige'  # Default-Wert

# Zuweisung zu Hundekopf
#trips_hundekopf = gpd.sjoin(trips_with_area, hundekopf_gdf, how='left', predicate='within')
# Debug-Ausgabe: Anzahl der Einträge nach Hundekopf-Zuweisung
trips_with_area.loc[trips_with_area.geometry.within(hundekopf_gdf.unary_union), 'RingOderAussen'] = 'Hundekopf'

print(f"Anzahl der Einträge im Hundekopf-Bereich: {trips_with_area['RingOderAussen'].eq('Hundekopf').sum()}")

output_file_path_hundekopf = os.path.join(script_dir, 'trips_with_person_info_updated_Wohnort_Maut_ClusterUnfertig300_Hundekopf.csv')
trips_with_area.to_csv(output_file_path_hundekopf, index=False)

trips_with_area.loc[trips_with_area.geometry.within(aussenbezirke_gdf.unary_union), 'RingOderAussen'] = 'Außenbezirke'
print(f"Anzahl der Einträge im Außenbezirke-Bereich: {trips_with_area['RingOderAussen'].eq('Außenbezirke').sum()}")

# Zuweisung zu Außenbezirke
#trips_außenbezirke = gpd.sjoin(trips_with_area, aussenbezirke_gdf, how='left', predicate='within')
# Debug-Ausgabe: Anzahl der Einträge nach Außenbezirke-Zuweisung
#print(f"Anzahl der Einträge im Außenbezirke-Bereich: {trips_außenbezirke['RingOderAussen'].eq('Außenbezirke').sum()}")

#trips_with_area.loc[trips_außenbezirke.index, 'RingOderAussen'] = 'Außenbezirke'

# Ergebnisse speichern
output_file_path = os.path.join(script_dir, 'trips_with_person_info_updated_Wohnort_Maut_ClusterUnfertig400.csv')
trips_with_area.to_csv(output_file_path, index=False)

print("Die Zuordnung der Wohnorte wurde aktualisiert und in 'trips_with_person_info_updated.csv' gespeichert.")
