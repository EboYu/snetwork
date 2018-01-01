package org.graph.graph;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Random;


public class DAGGraph {
    public Map<Long,Node> nodeList;
    public Map<Long,List<Edge>> outEdges;
    public Map<Long,List<Edge>> inEdges;
    public int nodeNums;
    public double r,s;
    public double step3probability = 60; //*************************************
    public double step1probability = 60;
    public List<Long> nodeNoIn;


    public DAGGraph(int nodeNum,double r,double s, double step1probability, double step3probability){
        nodeList = new LinkedHashMap<>();
        outEdges = new LinkedHashMap<>();
        inEdges = new LinkedHashMap<>();
        nodeNoIn = new LinkedList<>();
        this.nodeNums = nodeNum;
        this.r = r;
        this.s = s;
        this.step1probability = step1probability;
        this.step3probability = step3probability;
    }

    public void setNodeNums(int nodeNums) {
        this.nodeNums = nodeNums;
    }

    public void addNode(Node node){
        nodeList.put(node.NodeID,node);
    }

    public void addEdge(Node source, Node target){
        Edge edge = new Edge(source,target);
        if(outEdges.containsKey(source.NodeID)){
            List<Edge> outedges = outEdges.get(source.NodeID);
            outedges.add(edge);
            outEdges.replace(source.NodeID,outedges);
        }else {
            List<Edge> outedges = new LinkedList<>();
            outedges.add(edge);
            outEdges.put(source.NodeID,outedges);
        }

        if(inEdges.containsKey(target.NodeID)){
            List<Edge> inedges = inEdges.get(target.NodeID);
            inedges.add(edge);
            inEdges.replace(target.NodeID,inedges);
        }else {
            List<Edge> inedges = new LinkedList<>();
            inedges.add(edge);
            inEdges.put(target.NodeID,inedges);
        }
    }

    public void addEdge(long sourceID,long targetID){
        Node sourceNode;
        Node targetNode;
        if(!nodeList.containsKey(sourceID)){
            sourceNode= new Node(false,sourceID,r,s);
            addNode(sourceNode);
        }else sourceNode = nodeList.get(sourceID);

        if(!nodeList.containsKey(targetID)){
            targetNode= new Node(false,targetID,r,s);
            addNode(targetNode);
        }else targetNode = nodeList.get(targetID);
        addEdge(sourceNode,targetNode);
    }

    public int getNodeIndegree(Node node){
        return inEdges.get(node.NodeID).size();
    }

    public int getNodeOutdegree(Node node){
        return outEdges.get(node.NodeID).size();
    }


    public Map<Long, List<Edge>> getInEdges() {
        return inEdges;
    }


    public void staticOutDegree(){
        if(nodeList.isEmpty()|| outEdges.isEmpty())
            return;
        Map<Integer,Integer> degreeNode = new HashMap<>();

        outEdges.forEach((k,v)->{
            if(degreeNode.containsKey(v.size())){
                int num = degreeNode.get(v.size());
                num+=1;
                degreeNode.replace(v.size(),num);
            }else {
                degreeNode.put(v.size(),1);
            }
        });

        List<Map.Entry<Integer,Integer>> entries = new ArrayList<Map.Entry<Integer,Integer>>(degreeNode.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        final File output = new File("outDegree.txt");
        try {
            final PrintWriter writer = new PrintWriter(output);
            degreeNode.forEach((k,v)->{
                writer.write(k+"  "+v+"\n");
            });
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void staticInDegree(){
        if(nodeList.isEmpty()|| inEdges.isEmpty())
            return;
        Map<Integer,Integer> degreeNode = new HashMap<>();

        inEdges.forEach((k,v)->{
            if(degreeNode.containsKey(v.size())){
                int num = degreeNode.get(v.size());
                num+=1;
                degreeNode.replace(v.size(),num);
            }else {
                degreeNode.put(v.size(),1);
            }
        });

        List<Map.Entry<Integer,Integer>> entries = new ArrayList<Map.Entry<Integer,Integer>>(degreeNode.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        final File output = new File("inDegree.txt");
        try {
            final PrintWriter writer = new PrintWriter(output);
            degreeNode.forEach((k,v)->{
                writer.write(k+"  "+v+"\n");
            });
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void update1Graph(){
        Random random = new Random();
        for(Node node:nodeList.values()){
            int data = random.nextInt(100);
            node.prior = node.posteior;
            if(node.position){
                if(data<60) node.signal = true;
                else node.signal = false;
            }else {
                if(data<60) node.signal = false;
                else node.signal = true;
            }

        }
    }

    public Map<Integer,List<Long>>  update2Graph(){
        Map<Integer,List<Long>> expectations = new HashMap<>();
        List<Long> bullishNodes = new LinkedList<>();
        List<Long> bearishNodes = new LinkedList<>();
        BetaDistribution betaDistribution;
        for(int i=0; i<nodeNums;i++){
            Node node = this.nodeList.get((long)i);
            int[] PNnum= PNNodeNum(node);
            if(node.signal)
                PNnum[0] += 1;
            else
                PNnum[1] += 1;
            //node.r += PNnum[0];
            //node.s += PNnum[1];
            node.setPnum(PNnum[0]);
            node.setNnum(PNnum[1]);

            betaDistribution = new BetaDistribution(node.r+PNnum[0],node.s+PNnum[1]);
            node.setPosteior(1-betaDistribution.cumulativeProbability(0.5));
            if(node.posteior>0.5){
                node.expectation =true;
                bullishNodes.add(node.NodeID);
            }else if(node.posteior==0.5){
                node.expectation= node.signal;
                if(node.expectation)
                    bullishNodes.add(node.NodeID);
                else
                    bearishNodes.add(node.NodeID);
            }else{
                node.expectation =false;
                bearishNodes.add(node.NodeID);
            }
            nodeList.replace((long)i,node);
        }
        expectations.put(0,bullishNodes);
        expectations.put(1,bearishNodes);
        return expectations;
    }

    public int[] PNNodeNum(Node node){
        int[] PNnum = new int[2];
        if(outEdges.containsKey(node.NodeID)){
            List<Edge> edgeList = outEdges.get(node.NodeID);
            int pNum =0;
            int nNum =0;
            for(Edge edge:edgeList){
                if(nodeList.get(edge.targetID).signal)
                    pNum++;
                else nNum++;
            }
            PNnum[0]=pNum;
            PNnum[1]=nNum;
        }else {
            PNnum[0]=0;
            PNnum[1]=0;
        }

        return PNnum;
    }

    public void update3Graph(Map<Integer,List<Long>> expectations, double mean1, double sd1){
        // dispatch rewards
        NormalDistribution normalDistribution = new NormalDistribution(mean1,sd1);
        double[] returns, tempreturns, temp2returns;
        int i =0;
        /**
         if(!expectations.get(0).isEmpty()){
         returns = normalDistribution.sample(expectations.get(0).size());
         i =0;
         for(Long nodeId:expectations.get(0)){
         Node node  = nodeList.get(nodeId);
         node.setReward(returns[i]);
         nodeList.replace(nodeId,node);
         i++;
         }
         }

         normalDistribution = new NormalDistribution(mean2,sd2);
         if(!expectations.get(1).isEmpty()){
         returns = normalDistribution.sample(expectations.get(1).size());
         i =0;
         for(Long nodeId:expectations.get(1)){
         Node node  = nodeList.get(nodeId);
         node.setReward(returns[i]);
         nodeList.replace(nodeId,node);
         i++;
         }
         }
         */
        returns = normalDistribution.sample(nodeNums);
        temp2returns = returns;
        if(r == s){
            int cycle = 0;
            if(!expectations.get(0).isEmpty()){
                for(Long nodeId:expectations.get(0)) {
                    Node node = nodeList.get(nodeId);
                    node.setReward(temp2returns[cycle]);
                    cycle++;
                    nodeList.replace(nodeId,node);
                }
            }
            if(!expectations.get(1).isEmpty()){
                for(Long nodeId:expectations.get(1)) {
                    Node node = nodeList.get(nodeId);
                    node.setReward(temp2returns[cycle]);
                    cycle++;
                    if(cycle >= nodeNums)
                        break;
                    nodeList.replace(nodeId,node);
                }
            }

        }


        int middle;
        tempreturns = returns;
        orderNum(tempreturns);//the value from the larger to the smaller

        double absence = 10000.00000005;

        if(r < s) {
            middle = expectations.get(1).size();
            //System.out.println("*******middle:"+middle);

            if (!expectations.get(1).isEmpty()) {
                //returns = normalDistribution.sample(expectations.get(0).size());
                //i =0;
                int xxx = 0;
                int yyy = 0;
                for (Long nodeId : expectations.get(1)) {
                    Node node = nodeList.get(nodeId);
                    int max = 100;
                    int min = 0;
                    Random random = new Random();

                    int s = random.nextInt(max);
                    if (s <= step3probability && xxx < middle) {
                        int ss = random.nextInt(middle);
                        //System.out.println("The value of ss is:" + ss);
                        int www = 0;
                        for (int sss = ss; sss < middle; sss = (sss + 1) % middle) {
                            if (tempreturns[sss] != absence) {
                                node.setReward(tempreturns[sss]);
                                tempreturns[sss] = absence;
                                break;
                            }
                            if (www >= middle)
                                break;
                            www++;
                        }
                        xxx++;
                    } else {
                        if (yyy < nodeNums-middle) {
                            int ttttt = 0;
                            int dd = random.nextInt(nodeNums) % (nodeNums - middle + 1) + middle;
                            while (tempreturns[dd - 1] == absence ) {
                                dd = dd + 1;
                                if (dd > nodeNums)
                                    dd = dd - nodeNums + middle - 1;
                                if(ttttt >= (nodeNums - middle)) {
                                    int ssss = random.nextInt(middle);
                                    //System.out.println("The value of ss is:" + ss);
                                    int wwww = 0;
                                    for (int sss = ssss; sss < middle; sss = (sss + 1) % middle) {
                                        if (tempreturns[sss] != absence) {
                                            node.setReward(tempreturns[sss]);
                                            tempreturns[sss] = absence;
                                            break;
                                        }
                                        if (wwww >= middle)
                                            break;
                                        wwww++;
                                    }
                                    break;
                                }
                                ttttt++;
                            }
                            if(tempreturns[dd - 1] != absence) {
                                node.setReward(tempreturns[dd - 1]);
                                tempreturns[dd - 1] = absence;
                            }
                            yyy++;
                        } else {
                            int ss = random.nextInt(middle);
                            //System.out.println("The value of ss is:" + ss);
                            int www = 0;
                            for (int sss = 0; sss < middle; sss = (ss + 1) % middle) {
                                if (tempreturns[sss] != absence) {
                                    node.setReward(tempreturns[sss]);
                                    tempreturns[sss] = absence;
                                    break;
                                }
                                if (www >= middle)
                                    break;
                                www++;
                            }
                        }
                    }
                    //node.setReward(returns[i]);
                    nodeList.replace(nodeId, node);
                    //i++;
                }
            }


            if (!expectations.get(0).isEmpty()) {
                //returns = normalDistribution.sample(expectations.get(0).size());
//            i =0;

                for (Long nodeId : expectations.get(0)) {
                    int iii = 0;
                    Node node = nodeList.get(nodeId);
                    Random random2 = new Random();
                    int mm = random2.nextInt(nodeNums);
                    while (tempreturns[mm] == absence) {
                        mm = (mm + 1) % nodeNums;
                        if (iii == nodeNums) {
                            System.out.println("****************************");
                            break;
                        }
                        iii = iii + 1;
                    }
                    node.setReward(tempreturns[mm]);
                    tempreturns[mm] = absence;

                    //node.setReward(returns[i]);
                    nodeList.replace(nodeId, node);
                    //i++;
                }
            }
        }


        if(r > s) {
            middle = expectations.get(0).size();

            if (!expectations.get(0).isEmpty()) {
                //returns = normalDistribution.sample(expectations.get(0).size());
                //i =0;
                int xxx = 0;
                int yyy = 0;
                for (Long nodeId : expectations.get(0)) {
                    Node node = nodeList.get(nodeId);
                    int max = 100;
                    int min = 0;
                    Random random = new Random();

                    int s = random.nextInt(max);
                    if (s <= step3probability && xxx < middle) {
                        int ss = random.nextInt(middle);
                        //System.out.println("The value of ss is:" + ss);
                        int www = 0;
                        for (int sss = ss; sss < middle; sss = (sss + 1) % middle) {
                            if (tempreturns[sss] != absence) {
                                node.setReward(tempreturns[sss]);
                                tempreturns[sss] = absence;
                                break;
                            }
                            if (www >= middle)
                                break;
                            www++;
                        }
                        xxx++;
                    } else {
                        if (yyy < nodeNums-middle) {
                            int ttttt = 0;
                            int dd = random.nextInt(nodeNums) % (nodeNums - middle + 1) + middle;
                            while (tempreturns[dd - 1] == absence ) {
                                dd = dd + 1;
                                if (dd > nodeNums)
                                    dd = dd - nodeNums + middle - 1;
                                if(ttttt >= (nodeNums - middle)) {
                                    int sssss = random.nextInt(middle);
                                    //System.out.println("The value of ss is:" + ss);
                                    int wwwww = 0;
                                    for (int sss = sssss; sss < middle; sss = (sss + 1) % middle) {
                                        if (tempreturns[sss] != absence) {
                                            node.setReward(tempreturns[sss]);
                                            tempreturns[sss] = absence;
                                            break;
                                        }
                                        if (wwwww >= middle)
                                            break;
                                        wwwww++;
                                    }
                                    break;
                                }
                                ttttt++;
                            }
                            if(tempreturns[dd - 1] != absence) {
                                node.setReward(tempreturns[dd - 1]);
                                tempreturns[dd - 1] = absence;
                            }
                            yyy++;
                        } else {
                            int ss = random.nextInt(middle);
                            //System.out.println("The value of ss is:" + ss);
                            int www = 0;
                            for (int sss = 0; sss < middle; sss = (ss + 1) % middle) {
                                if (tempreturns[sss] != absence) {
                                    node.setReward(tempreturns[sss]);
                                    tempreturns[sss] = absence;
                                    break;
                                }
                                if (www >= middle)
                                    break;
                                www++;
                            }
                        }
                    }
                    //node.setReward(returns[i]);
                    nodeList.replace(nodeId, node);
                    //i++;
                }
            }


            if (!expectations.get(1).isEmpty()) {
                //returns = normalDistribution.sample(expectations.get(0).size());
//            i =0;

                for (Long nodeId : expectations.get(1)) {
                    int iii = 0;
                    Node node = nodeList.get(nodeId);
                    Random random2 = new Random();
                    int mm = random2.nextInt(nodeNums);
                    while (tempreturns[mm] == absence) {
                        mm = (mm + 1) % nodeNums;
                        if (iii == nodeNums) {
                            System.out.println("****************************");
                            break;
                        }
                        iii = iii + 1;
                    }
                    node.setReward(tempreturns[mm]);
                    tempreturns[mm] = absence;

                    //node.setReward(returns[i]);
                    nodeList.replace(nodeId, node);
                    //i++;
                }
            }
        }




        for(int j=0; j<nodeNums;j++){
            Node node = this.nodeList.get((long)j);
            node.position = PositionChoice(node);
            //nodeList.replace((long)j,node);
        }
    }

    public static void orderNum(double []n) {

        for (int i = 0; i < n.length - 1; i++) {
            for (int j = 0; j < n.length - 1 - i; j++) {
                double temp = 0;
                if (n[j] < n[j + 1]) {
                    temp = n[j + 1];
                    n[j + 1] = n[j];
                    n[j] = temp;
                }
            }
        }
    }

    public boolean PositionChoice(Node node){

        if(!outEdges.containsKey(node.NodeID)){
            node.setBuNum(0);
            node.setBeNum(0);
            return node.expectation;
        }
        List<Edge> edgeList = outEdges.get(node.NodeID);
        double buReward =0;
        double beReward =0;
        int buNum = 0 , beNum = 0;
        for(Edge edge:edgeList){
            if(nodeList.get(edge.targetID).expectation){
                buReward+=nodeList.get(edge.targetID).reward;
                buNum++;
            }
            else {
                beReward+=nodeList.get(edge.targetID).reward;
                beNum++;
            }
        }
        node.setBuNum(buNum);
        node.setBeNum(beNum);
        if(beReward!=0 && beNum!=0 && buNum!=0 && buReward!=0){
            if(buReward/buNum>beReward/beNum)// if rewared mean of bullish nodes
                return true;
            else if(beReward/beNum==buReward/buNum){
                if(node.reward==1)
                    return true;
                else return false;
            }else return false;
        }else if(beNum==0 && buNum!=0){
            return true;
        }else if(buNum==0 && beNum!=0){
            return false;
        }
        return false;

    }
}