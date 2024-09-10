import pandas as pd
import geopandas as gpd
from shapely.geometry import Point
import os

# Dateipfade
script_dir = os.path.dirname(__file__)
trips_file_path = os.path.join(script_dir, 'trips_with_person_info_BaseCase_Cluster400.csv.gz')
hundekopf_shapefile_path = os.path.join(script_dir, 'hundekopf', 'hundekopfNeu.shp')
aussenbezirke_shapefile_path = os.path.join(script_dir, 'aussenbezirke', 'aussenbezirkeNeu.shp')

# CSV-Datei und Shapefiles laden
trips_df = pd.read_csv(trips_file_path)
hundekopf_gdf = gpd.read_file(hundekopf_shapefile_path).to_crs(epsg=25832)
aussenbezirke_gdf = gpd.read_file(aussenbezirke_shapefile_path).to_crs(epsg=25832)

# Geodatenframe für Trips erstellen
trips_gdf = gpd.GeoDataFrame(
    trips_df,
    geometry=gpd.points_from_xy(trips_df['home_x'], trips_df['home_y']),
    crs='EPSG:25832'
)

# Debug-Ausgaben: CRS überprüfen
print("Hundekopf CRS:", hundekopf_gdf.crs)
print("Außenbezirke CRS:", aussenbezirke_gdf.crs)
print("Trips CRS: EPSG:25832")

# Überprüfen, ob Punkte innerhalb von Hundekopf liegen
def check_within_area(trips_gdf, area_gdf, area_name):
    # Prüfen, ob die Punkte innerhalb der Geometrien liegen
    trips_with_area = trips_gdf.copy()
    trips_with_area['area_type'] = 'Sonstige'  # Default-Wert
    for idx, point in trips_with_area.iterrows():
        if area_gdf.contains(point['geometry']).any():
            trips_with_area.at[idx, 'area_type'] = area_name
    return trips_with_area

# Überprüfen für Hundekopf
trips_with_hundekopf = check_within_area(trips_gdf, hundekopf_gdf, 'Hundekopf')
# Überprüfen für Außenbezirke
trips_with_aussenbezirke = check_within_area(trips_gdf, aussenbezirke_gdf, 'Außenbezirke')

# Merging der Ergebnisse
trips_with_area = pd.merge(trips_with_hundekopf, trips_with_aussenbezirke[['geometry', 'area_type']], on='geometry', how='left', suffixes=('', '_aussenbezirke'))
trips_with_area['area_type'] = trips_with_area['area_type'].fillna(trips_with_area['area_type_aussenbezirke'])
trips_with_area = trips_with_area.drop(columns=['area_type_aussenbezirke'])

# Ergebnisse speichern
output_file_path = os.path.join(script_dir, 'trips_with_person_info_updated_Wohnort_BaseCase_Cluster400.csv.gz')
trips_with_area.to_csv(output_file_path, index=False)

print("Die Zuordnung der Wohnorte wurde aktualisiert und in 'trips_with_person_info_updated.csv' gespeichert.")

# Debug-Ausgabe: Überprüfen der Zuweisungen
print("Zuweisungen für Hundekopf:")
print(trips_with_area[trips_with_area['area_type'] == 'Hundekopf'].head())
print("Zuweisungen für Außenbezirke:")
print(trips_with_area[trips_with_area['area_type'] == 'Außenbezirke'].head())
print("Zuweisungen für Sonstige:")
print(trips_with_area[trips_with_area['area_type'] == 'Sonstige'].head())
