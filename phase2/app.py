import pandas as pd
import networkx as nx
import matplotlib.pyplot as plt


def draw_graph():
    node_size = 60
    edge_width = 1
    dpi = 300

    df = pd.read_csv("lexi.csv")

    graph = nx.DiGraph()
    print('Creating Graph ...')

    for index, row in df.iterrows():
        class_name = row[2]
        association_class = row[19]
        aggregation_class = row[20]
        delegation_class = row[21]
        composition_class = row[22]
        graph.add_node(class_name)
        if not pd.isna(composition_class):
            graph.add_edge(class_name, composition_class, label='1')
        if not pd.isna(aggregation_class):
            graph.add_edge(class_name, aggregation_class, label='2')
        if not pd.isna(delegation_class):
            graph.add_edge(class_name, delegation_class, label='3')
        if not pd.isna(association_class):
            graph.add_edge(class_name, association_class, label='4')

    degrees = dict(graph.degree())
    colors = ['orange' if degrees[node] == 0 else 'cyan' for node in graph.nodes()]

    pos = nx.spring_layout(graph, k=0.3, iterations=20)
    fig = plt.figure(figsize=(60, 60))
    nx.draw(graph, pos, with_labels=True, node_size=node_size, width=edge_width, node_color=colors, arrows=True)

    edge_labels = nx.get_edge_attributes(graph, 'label')
    nx.draw_networkx_edge_labels(graph, pos, edge_labels=edge_labels)

    print('Saving Output ...')
    plt.savefig('lexi-graph.png', dpi=dpi)
    plt.show()
    print('Done')


if __name__ == '__main__':
    draw_graph()
