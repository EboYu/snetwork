package org.graph.util;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.graph.graph.DAGGraph;
import org.graph.graph.Node;

import java.util.Iterator;
import java.util.Random;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Node;

public class GraphGenerator {


    public static DAGGraph graphGenerator(int nodeNum, double[] betaDistribution, double r, double s, double step1probability, double step3probability, double r1, double s1, double threshold){
        DAGGraph DAGGraph = new DAGGraph(nodeNum,r,s, step1probability, step3probability);
        //initialize graph
        BetaDistribution beta = new BetaDistribution(r1,s1);
        double[] betaDistribution4InOut = beta.sample(nodeNum);
        for(int i =0;i<nodeNum;i++){
            Node node;
            if(betaDistribution[i]>=0.5){
                node = new Node(true,i,r,s);
            }else node = new Node(false,i,r,s);
            node.setPrior(betaDistribution[i]);
            node.setProIn(betaDistribution4InOut[i]);
            node.setProOut(1-betaDistribution4InOut[i]);
            DAGGraph.addNode(node);

        }
        for(int i =0;i<nodeNum;i++){
            for(int j=0;j<nodeNum;j++){
                if(i!=j && nodeNum-i-1+j!=999){
                    Random random = new Random();
                    int randomint = random.nextInt(100);
                    if(DAGGraph.nodeList.get((long)j).proIn>DAGGraph.nodeList.get((long)i).proIn && randomint >= threshold)
                        DAGGraph.addEdge(i,j);
                }
            }
        }

        for(int i=0;i<nodeNum;i++){
            if(!DAGGraph.inEdges.containsKey((long)i)){
                DAGGraph.nodeNoIn.add((long)i);
            }
        }

        /*for(int i =0;i<nodeNum;i++){
            for(int j=0;j<nodeNum;j++){
                if(i!=j && nodeNum-i-1+j!=999){
                    if(inmatrix[j][i]==1){
                        DAGGraph.addEdge(j,i);
                    }
                    if(outmatrix[j][nodeNum-i-1]==1){
                        DAGGraph.addEdge(i,j);
                    }
                }

            }
        }
        Random random = new Random();
        for(int i =0;i<nodeNum;i++){
            Node node = DAGGraph.nodeList.get((long)i);
            int outNum = node.tempOutNum;
            for(int j=0;j<outNum;j++){
                int nodeIndex;
                while (true){
                    nodeIndex = random.nextInt(nodeNum);
                    if(nodeIndex!=i) {
                        Node candidateNode = DAGGraph.nodeList.get((long)nodeIndex);
                        if(candidateNode.tempInNum!=0){
                            DAGGraph.addEdge(i,nodeIndex);
                            candidateNode.tempInNum--;
                            node.tempOutNum--;
                            //DAGGraph.nodeList.replace((long)nodeIndex, candidateNode);
                            break;
                        }
                    }
                }

            }
        }*/

        return DAGGraph;
    }


    public static int InDegreeNum(int[][]matrix,int id){
        int num=0;
        for(int i=0;i<matrix.length;i++){
            if(i!=id)
                num+=matrix[i][id];
        }
        return num;
    }

    public static int OutDegreeNum(int[][]matrix,int id){
        int num=0;
        for(int i=0;i<matrix.length;i++){
            if(i+id!=999)
                num+=matrix[i][id];
        }
        return num;
    }


}