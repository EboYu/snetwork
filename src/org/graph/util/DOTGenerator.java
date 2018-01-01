package org.graph.util;


import org.graph.graph.DAGGraph;
import org.graph.graph.Edge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DOTGenerator {
    public static String model2DOTGraph(DAGGraph graph, String filename){
        final StringBuilder graphBuilder = new StringBuilder( "DAGGraph \""+mangleNodeName("Network")+"()\" {\n" );
        String label;
        String shape = "shape=circle";

        for(int i=0;i<graph.nodeList.size();i++){
            label = String.valueOf(i);
            label += "\\l";
            label="label=\""+label+"\"";
            graphBuilder.append("   "+String.valueOf(i)+" ["+shape+","+label+"]\n");
        }

        Iterator<Map.Entry<Long,List<Edge>>> iterator = graph.inEdges.entrySet().iterator();
        while (iterator.hasNext()){
            List<Edge> edgeList = iterator.next().getValue();
            Iterator<Edge> iterator1 = edgeList.iterator();
            while (iterator1.hasNext()){
                Edge edge = iterator1.next();
                graphBuilder.append( "    "+String.valueOf(edge.sourceID)+" -> "+String.valueOf(edge.targetID)+"\n" );
            }
        }


        final File output = new File(filename);
        String dot = graphBuilder.append("}").toString();
        try {
            final PrintWriter writer = new PrintWriter(output);
            writer.write(dot);
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }




        return graphBuilder.toString();
    }


    public static final String mangleNodeName(String id) {
        return id.replace("<", "" ).replace(">", "");
    }

}