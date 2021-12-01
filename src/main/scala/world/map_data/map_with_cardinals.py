import pandas as pd
import pyproj

edges = pd.read_csv("processed/map.edge", header=None, names=['start', 'end', 'length', 'bidirectional', 'name'])
nodes = pd.read_csv("processed/map.node", header=None, names=['id', 'lat', 'lon'])

edges_list = []
geodesic = pyproj.Geod(ellps='WGS84')

for i, row in edges.iterrows():
    source = row.loc['start']
    target = row.loc['end']
    oneway = row.loc['bidirectional']
    name = row.loc['name']
    
    source_lat = nodes.iloc[source, 1]
    source_lon = nodes.iloc[source, 2]
    target_lat = nodes.iloc[target, 1]
    target_lon = nodes.iloc[target, 2]
    
    azi, back_azimuth, distance = geodesic.inv(source_lon, source_lat, target_lon, target_lat)
    
    if azi < -157.5 or azi > 157.7:
        cardinal = "South"
    elif azi < -112.5:
        cardinal = "Southwest"
    elif azi < -67.5:
        cardinal = "West"
    elif azi < -22.5:
        cardinal = "Northwest"
    elif azi < 22.5:
        cardinal = "North"
    elif azi < 67.5:
        cardinal = "Northeast"
    elif azi < 112.5:
        cardinal = "East"
    else:
        cardinal = "Southeast"
    
    edges_list.append([source, target, oneway, name, cardinal, distance])
    
    if i % 1000 == 0:
        print(round(i/len(edges.index)*100, 2), end="\r")
        
print(100.00, end="\r")

edges_list_df = pd.DataFrame(edges_list)
edges_list_df.to_csv('processed/map.edge', encoding='UTF-8', index=False, header=False)
