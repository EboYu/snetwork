package org.graph.util;


import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.graph.graph.DAGGraph;
import org.graph.graph.Edge;
import org.graph.graph.Node;

public class ReadWriteExcel {

    public static int[][] readData(File file){
        try {
            List<String[]> list = new LinkedList<>();
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            while (line!=null){
                list.add(line.split("\t"));
            }
            int[][] matrix = new int[list.size()][list.get(0).length];
            for(int i=0;i<list.size();i++){
                for(int j= 0;j<list.get(0).length;j++){
                    matrix[i][j]= Integer.valueOf(list.get(i)[j]);
                }
            }
            return matrix;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    public static int[][] readExcel1(File file){
        try{
            //ArrayList<ArrayList<Integer>> sheetArray = new ArrayList<ArrayList<Integer>> ();
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
            //HSSFWorkbook wb
            HSSFSheet sheet = wb.getSheetAt(0);
            int[][] matrix = new int[sheet.getPhysicalNumberOfRows()][sheet.getRow(0).getLastCellNum()];
            //<Integer> colList;
            HSSFRow row;
            HSSFCell cell;
            Integer value;
            for(int i = 0 , rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows() ; i++ ){
                row = sheet.getRow(i);
                //colList = new ArrayList<Integer>();
                if(row == null){
                    //if(i != sheet.getPhysicalNumberOfRows()){
                    //    sheetArray.add(colList);
                    //}
                    continue;
                }else{
                    rowCount++;
                }

                for( int j = 0 ; j <= row.getLastCellNum() ;j++){
                    cell = row.getCell(j);
                    if(cell.getCellType()==XSSFCell.CELL_TYPE_NUMERIC){
                        value = (int)cell.getNumericCellValue() ;
                    }else value =-1;
                    matrix[i][j] = value;
                    //colList.add(value);
                }//end for j
                //sheetArray.add(colList);
            }//end for i
            return matrix;
        }catch(Exception e){
            return null;
        }
    }

    public static int[][] readExcel(File file){
        try{
            //ArrayList<ArrayList<Integer>> sheetArray = new ArrayList<ArrayList<Integer>> ();
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
            //HSSFWorkbook wb
            XSSFSheet sheet = wb.getSheetAt(0);
            int[][] matrix = new int[sheet.getPhysicalNumberOfRows()][sheet.getRow(0).getLastCellNum()];
            //<Integer> colList;
            XSSFRow row;
            XSSFCell cell;
            Integer value;
            for(int i = 0 , rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows() ; i++ ){
                row = sheet.getRow(i);
                //colList = new ArrayList<Integer>();
                if(row == null){
                    //if(i != sheet.getPhysicalNumberOfRows()){
                    //    sheetArray.add(colList);
                    //}
                    continue;
                }else{
                    rowCount++;
                }

                for( int j = 0 ; j <= row.getLastCellNum() ;j++){
                    cell = row.getCell(j);
                    if(cell == null ){
                        if(j != row.getLastCellNum()){
                            //colList.add(-1);
                        }
                        continue;
                    }
                    if(cell.getCellType()==XSSFCell.CELL_TYPE_NUMERIC){
                        value = (int)cell.getNumericCellValue() ;
                    }else value =-1;
                    matrix[i][j] = value;
                    //colList.add(value);
                }//end for j
                //sheetArray.add(colList);
            }//end for i
            return matrix;
        }catch(Exception e){
            return null;
        }
    }

    public static void graph2Excel(DAGGraph graph, String fileName){
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = wb.createSheet("sheet0");
            SXSSFRow row;
            SXSSFCell cell;
            int []links = new int[graph.nodeNums];
            for(int i=0;i<graph.nodeNums;i++){
                links[i]=0;
            }
            for(int i=0;i<=graph.nodeNums;i++){
                row = sheet.createRow(i);
                if(i==0){
                    for(int j=0;j<graph.nodeNums;j++){
                        cell = row.createCell(j+1);
                        cell.setCellValue(j);
                    }
                }else {
                    cell = row.createCell(0);
                    cell.setCellValue(i-1);
                    List<Edge> outEdges = graph.outEdges.get((long)(i-1));

                    int[] tmplinks = new  int[graph.nodeNums];
                    tmplinks = Arrays.copyOf(links,graph.nodeNums);
                    if(outEdges!=null && !outEdges.isEmpty()){
                        for(Edge edge:outEdges){
                            tmplinks[(int)edge.targetID] = 1;
                        }
                    }

                    for(int j=0;j<graph.nodeNums;j++){
                        cell = row.createCell(j+1);
                        cell.setCellValue(tmplinks[j]);
                    }
                }
            }
            writeExcel(fileName,wb);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void insert1Graph2Excel(DAGGraph graph, SXSSFWorkbook wb, int sheetNum){
        try {
            SXSSFSheet sheet = wb.createSheet("sheet"+sheetNum);
            SXSSFRow row;
            SXSSFCell cell;
            for(int i=0;i<=graph.nodeNums;i++){
                row = sheet.createRow(i);
                if(i==0){
                    cell = row.createCell(0);
                    cell.setCellValue("Node ID");
                    cell = row.createCell(1);
                    cell.setCellValue("Node Indegree");
                    cell = row.createCell(2);
                    cell.setCellValue("Node Outdegree");
                    cell = row.createCell(3);
                    cell.setCellValue("Prior");
                    cell = row.createCell(4);
                    cell.setCellValue("Private Signal");
                    cell = row.createCell(5);
                    cell.setCellValue("Positive Num");
                    cell = row.createCell(6);
                    cell.setCellValue("Negative Num");
                    cell = row.createCell(7);
                    cell.setCellValue("Posterior");
                    cell = row.createCell(8);
                    cell.setCellValue("Expectation");
                    cell = row.createCell(9);
                    cell.setCellValue("Reward");
                    cell = row.createCell(10);
                    cell.setCellValue("Bullish Num");
                    cell = row.createCell(11);
                    cell.setCellValue("Bearish Num");
                    cell = row.createCell(12);
                    cell.setCellValue("Behavior");
                }else {
                    Node node = graph.nodeList.get((long)(i-1));
                    cell = row.createCell(0);
                    cell.setCellValue((double) node.NodeID);

                    cell = row.createCell(1);
                    if(graph.inEdges.containsKey(node.NodeID))
                        cell.setCellValue((double) graph.inEdges.get(node.NodeID).size());
                    else cell.setCellValue(0);

                    cell = row.createCell(2);
                    if(graph.outEdges.containsKey(node.NodeID))
                        cell.setCellValue((double) graph.outEdges.get(node.NodeID).size());
                    else cell.setCellValue(0);

                    cell = row.createCell(3);
                    cell.setCellValue(node.prior);

                    cell = row.createCell(4);
                    if(node.signal)
                        cell.setCellValue(1);
                    else cell.setCellValue(0);

                    cell = row.createCell(5);
                    cell.setCellValue((double) node.Pnum);

                    cell = row.createCell(6);
                    cell.setCellValue((double) node.Nnum);

                    cell = row.createCell(7);
                    cell.setCellValue(node.posteior);

                    cell = row.createCell(8);
                    if(node.expectation)
                        cell.setCellValue(1);
                    else cell.setCellValue(0);

                    cell = row.createCell(9);
                    cell.setCellValue(node.reward);

                    cell = row.createCell(10);
                    cell.setCellValue((double) node.BuNum);

                    cell = row.createCell(11);
                    cell.setCellValue((double) node.BeNum);

                    cell = row.createCell(12);
                    if(node.position)
                        cell.setCellValue(1);
                    else cell.setCellValue(0);
                }
            }
            sheet.flushRows();

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    public static void insertStatistic2Excel(DAGGraph graph, Map<Integer,List<Long>> expectations, SXSSFSheet sheet, int rowNum, double threshold1, double threshold2){
        SXSSFRow row = sheet.createRow(rowNum);
        SXSSFCell cell;
        if(rowNum ==0){
            cell = row.createCell(0);
            cell.setCellValue("T");
            cell = row.createCell(1);
            cell.setCellValue("Bullish Num");
            cell = row.createCell(2);
            cell.setCellValue("Bullish Indegree");
            cell = row.createCell(3);
            cell.setCellValue("Bearish Indegree");
            cell = row.createCell(4);
            cell.setCellValue("Bullish Reward");
            cell = row.createCell(5);
            cell.setCellValue("Bearish Reward");
            cell = row.createCell(6);
            cell.setCellValue("Long Position Num");
            cell = row.createCell(7);
            cell.setCellValue("20% Bullish Num");
            cell = row.createCell(8);
            cell.setCellValue("20% Bullish Reward");
            cell = row.createCell(9);
            cell.setCellValue("20% Bearish Reward");
            cell = row.createCell(10);
            cell.setCellValue("20% Long Position Num");
            cell = row.createCell(11);
            cell.setCellValue("80% Bullish Num");
            cell = row.createCell(12);
            cell.setCellValue("80% Bullish Reward");
            cell = row.createCell(13);
            cell.setCellValue("80% Bearish Reward");
            cell = row.createCell(14);
            cell.setCellValue("80% Long Position Num");
        }else {
            cell = row.createCell(0);
            cell.setCellValue(rowNum);

            cell = row.createCell(1);
            cell.setCellValue(expectations.get(0).size());

            double[] avgs = avgInDegreeReward(graph, expectations);

            cell = row.createCell(2);
            cell.setCellValue(avgs[0]);
            cell = row.createCell(3);
            cell.setCellValue(avgs[2]);
            cell = row.createCell(4);
            cell.setCellValue(avgs[1]);
            cell = row.createCell(5);
            cell.setCellValue(avgs[3]);
            cell = row.createCell(6);
            cell.setCellValue((int)avgs[4]);
            List<Double> avgs1 = avgIndegreeRewardwithScope(graph,new double[]{threshold1,threshold2});

            cell = row.createCell(7);
            cell.setCellValue(avgs1.get(0).intValue());
            cell = row.createCell(8);
            cell.setCellValue(avgs1.get(1));//"20% Bullish Reward");
            cell = row.createCell(9);
            cell.setCellValue(avgs1.get(2));//"20% Bearish Reward");
            cell = row.createCell(10);
            cell.setCellValue(avgs1.get(3).intValue());//"20% Long num"
            cell = row.createCell(11);
            cell.setCellValue(avgs1.get(4).intValue());//"80% Bullish Num");
            cell = row.createCell(12);
            cell.setCellValue(avgs1.get(5));//"80% Bullish Reward");
            cell = row.createCell(13);
            cell.setCellValue(avgs1.get(6));//"80% Bearish Reward");
            cell = row.createCell(14);
            cell.setCellValue(avgs1.get(7).intValue());//"80% Long Position Num");
        }
    }
    public static void writeExcel(String fileName, SXSSFWorkbook wb){

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            wb.write(os);
        } catch (IOException e){
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        File file = new File(fileName);
        OutputStream fos  = null;
        try
        {
            fos = new FileOutputStream(file);
            fos.write(content);
            os.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static double[] avgInDegreeReward(DAGGraph graph, Map<Integer,List<Long>> expectations){
        double[] avgs = new double[]{0,0,0,0,0};
        for(List<Long> expectation:expectations.values()){
            for(Long nodeId: expectation){
                Node node = graph.nodeList.get(nodeId);
                if(node.position) avgs[4]++;
                if(node.expectation) {
                    if(graph.inEdges.containsKey(nodeId)){
                        avgs[0]+=graph.inEdges.get(nodeId).size();
                        avgs[1]+=node.reward;
                    }

                }else {
                    if(graph.inEdges.containsKey(nodeId)){
                        avgs[2]+=graph.inEdges.get(nodeId).size();
                        avgs[3]+=node.reward;
                    }
                }
            }
        }
        avgs[0] = avgs[0]/expectations.get(0).size();
        avgs[1] = avgs[1]/expectations.get(0).size();
        avgs[2] = avgs[2]/expectations.get(1).size();
        avgs[3] = avgs[3]/expectations.get(1).size();
        return avgs;
    }

    public static List<Double> avgIndegreeRewardwithScope(DAGGraph graph, double[] scopes){
        Map<Long, List<Edge>> indegrees = graph.getInEdges();
        List<Map.Entry<Long, List<Edge>>> indegreeList = new ArrayList<Map.Entry<Long, List<Edge>>>(indegrees.entrySet());
        Collections.sort(indegreeList, new Comparator<Map.Entry<Long, List<Edge>>>() {
            @Override
            public int compare(Map.Entry<Long, List<Edge>> o1, Map.Entry<Long, List<Edge>> o2) {
                return o2.getValue().size()-o1.getValue().size();
            }
        });
        List<Double> avgs = new LinkedList<>();

        int preNum = (int)(scopes[0]*(double) indegreeList.size());
        double bullNum=0, bullReward=0, beaNum=0, beaReward=0, longNum=0;//0=bull num, 1=bull reward, 2= ber reward, 3= long
        for(int i =0; i<preNum;i++){
            Map.Entry<Long, List<Edge>> entry = indegreeList.get(i);
            Node node = graph.nodeList.get(entry.getKey());
            if(node.expectation) {
                bullNum++;
                bullReward+= node.reward;
            }else {
                beaNum++;
                beaReward+=node.reward;
            }
            if(node.position) longNum ++;
        }
        avgs.add(bullNum);
        if(bullNum!=0)
            avgs.add(bullReward/bullNum);
        else avgs.add(0.0);
        if(beaNum!=0)
            avgs.add(beaReward/beaNum);
        else avgs.add(0.0);
        avgs.add(longNum);

        bullNum=0;
        bullReward=0;
        beaNum=0;
        beaReward=0;
        longNum=0;
        int sufNum = (int)(scopes[1]*(double)indegreeList.size());
        for(int i = indegreeList.size()-sufNum-1;i<indegreeList.size();i++){
            Map.Entry<Long, List<Edge>> entry = indegreeList.get(i);
            Node node = graph.nodeList.get(entry.getKey());
            if(node.expectation) {
                bullNum++;
                bullReward+= node.reward;
            }else {
                beaNum++;
                beaReward+=node.reward;
            }
            if(node.position) longNum ++;
        }
        if(!graph.nodeNoIn.isEmpty()){
            for(Long nodeId:graph.nodeNoIn){
                Node node = graph.nodeList.get(nodeId);
                if(node.expectation) {
                    bullNum++;
                    bullReward+= node.reward;
                }else {
                    beaNum++;
                    beaReward+=node.reward;
                }
                if(node.position) longNum ++;
            }
        }
        avgs.add(bullNum);
        if(bullNum!=0)
            avgs.add(bullReward/bullNum);
        else avgs.add(0.0);
        if(beaNum!=0)
            avgs.add(beaReward/beaNum);
        else avgs.add(0.0);
        avgs.add(longNum);

        return avgs;
    }
}
