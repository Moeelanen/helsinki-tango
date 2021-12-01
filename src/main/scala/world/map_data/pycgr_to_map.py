import pandas as pd
import os

with open('raw/helsinki.pycgr', 'r') as file:
    data = file.readlines()
    node_count = int(data[7])
    edge_count = int(data[8])
    data = data[9:]
    
    
    node_data = data[:node_count]
    edge_data = data[-edge_count:]
    
    for i, node in enumerate(node_data):
        a, b, c = node.split(' ')
        node_data[i] = [int(a), float(b), float(c)]
        
    for i, edge in enumerate(edge_data):
        source, target, length, road_type, limit, bidirectional = edge.split(' ')
        edge_data[i] = [int(source), int(target), float(length), not (bool(int(bidirectional)))]
        

with open('raw/helsinki.pycgr_names', 'r') as name_file:
    name_data = name_file.readlines()
    for i, line in enumerate(name_data):
        name_data[i] = line[:-1]
        if (i % 10000 == 0):
            print(i)
            
node_df = pd.DataFrame(node_data, columns=['id', 'lat', 'lon'])
edge_df = pd.DataFrame(edge_data, columns=['source', 'target', 'length', 'bidirectional'])
name_df = pd.DataFrame(name_data, columns=['name'])
edge_df = edge_df.join(name_df)

node_df.to_csv('processed/map.node', encoding='UTF-8', index=False, header=False)
edge_df.to_csv('processed/map.edge', encoding='UTF-8', index=False, header=False)