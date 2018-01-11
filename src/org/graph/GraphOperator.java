package org.graph;
import java.io.*;
import java.util.*;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.graph.graph.DAGGraph;
import org.graph.util.GraphGenerator;
import org.graph.util.ReadWriteExcel;

import static org.graph.util.DOTGenerator.mangleNodeName;
import static org.graph.util.ReadWriteExcel.graph2Excel;


public class GraphOperator {
    public static StringBuilder graphBuilder =  new StringBuilder( "digraph \""+mangleNodeName("Network")+"()\" {\n" );
    public BufferedWriter outWriter = null;
    public static String shape = "shape=box";

    public static DAGGraph graph;

    public static void buildGraphNode(int num){

        String label;
        String shape = "shape=point";

        for(int i=0;i<num;i++){
            label = String.valueOf(i);
            label += "\\l";
            label="label=\""+label+"\"";
            graphBuilder.append("   "+String.valueOf(i)+" ["+shape+","+label+"]\n");
        }
    }

    public static void main(String[] args) throws Exception {

        double r = 1.5;
        double s = 1.3;
        double mean1 = 0;
        double sd1 = 1;
        int iteratorNums =100;
        double step3probability = 99; //*************************************
        double step1probability = 70;
        double r4Inout =2;
        double s4Inout =3;
        double threshold4Inout = 79;
        double thre1 =0.2;
        double thre2 = 0.8;
        int nodeNum =1000;

        SXSSFWorkbook wb = new SXSSFWorkbook();
        SXSSFWorkbook wb1 = new SXSSFWorkbook();
        SXSSFSheet sheet = wb1.createSheet("sheet1");
        ReadWriteExcel.insertStatistic2Excel(null,null,sheet,0,thre1,thre2);//initialize the excel
        //ReadWriteExcel.readData(new File("/home/yinboyu/workspace/1.txt"));

        //int[][] inmatrix = ReadWriteExcel.readExcel(new File("/home/yinboyu/workspace/data5040_1.xlsx"));
        //int[][] outmatrix = ReadWriteExcel.readExcel(new File("/home/yinboyu/workspace/data5040_2.xlsx"));

        BetaDistribution betaDistribution = new BetaDistribution(r,s);
        //double result = 1- betaDistribution.cumulativeProbability(0.5);
        double[] alphas = betaDistribution.sample(nodeNum);


        DAGGraph graph = GraphGenerator.graphGenerator(nodeNum,alphas,r,s,step1probability,step3probability,r4Inout,s4Inout,threshold4Inout);
        graph2Excel(graph,"graph.xlsx");

        //inmatrix = null;
        //outmatrix = null;
        alphas = null;
        //model2DOTGraph(graph,"topology.dot");

        Map<Integer,List<Long>> expectations= graph.update2Graph();
        graph.update3Graph(expectations,mean1,sd1);
        //List<DiGraph> graphList = new LinkedList<>();
        //graphList.add(graph);
        ReadWriteExcel.insert1Graph2Excel(graph,wb,1);
        ReadWriteExcel.insertStatistic2Excel(graph,expectations,sheet,1,thre1,thre2);


        for(int t=2;t<=iteratorNums;t++){
            graph.update1Graph();
            expectations= graph.update2Graph();
            graph.update3Graph(expectations,mean1,sd1);
            //graphList.add(graph);
            ReadWriteExcel.insert1Graph2Excel(graph,wb,t);
            ReadWriteExcel.insertStatistic2Excel(graph,expectations,sheet,t,thre1,thre2);
        }
        /*int index1 =0;
        for(DiGraph graph1:graphList){
            ReadWriteExcel.insert1Graph2Excel(graph1,wb,index1);
            index1++;
        }*/
        ReadWriteExcel.writeExcel("BULLISH99_100.xlsx",wb);
        ReadWriteExcel.writeExcel("BULLISH99_SUM.xlsx",wb1);
        return;
    }
}