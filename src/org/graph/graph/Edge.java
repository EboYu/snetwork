package org.graph.graph;


public class Edge {
    public long sourceID;
    public long targetID;

    public Edge(Node source, Node target){
        sourceID = source.NodeID;
        targetID = target.NodeID;
    }
}