package org.graph.graph;

public class Node {
    public boolean signal;
    public boolean expectation;
    public boolean position;
    public double reward;
    public long NodeID;
    public int tempInNum;
    public int tempOutNum;
    public double r,s;
    public double prior;
    public double posteior;
    public int Pnum;
    public int Nnum;
    public int BuNum;
    public int BeNum;
    public double proIn;
    public double proOut;

    public Node(boolean value, long nodeID,double r,double s){
        this.signal = value;
        this.NodeID = nodeID;
        position = false;
        expectation = false;
        reward = 0;
        this.r = r;
        this.s = s;
    }

    public Node(boolean value, long nodeID, int tempInNum, int tempOutNum,double r,double s){
        this.signal = value;
        this.NodeID = nodeID;
        this.tempOutNum = tempOutNum;
        this.tempInNum = tempInNum;
        position = false;
        expectation = false;
        reward = 0;
        this.r = r;
        this.s = s;
        this.proIn =0 ;
        this.proOut=0;
    }

    public void setProIn(double proIn) {
        this.proIn = proIn;
    }

    public void setProOut(double proOut) {
        this.proOut = proOut;
    }

    public void setNnum(int nnum) {
        Nnum = nnum;
    }

    public void setPnum(int pnum) {
        Pnum = pnum;
    }

    public void setPosteior(double posteior) {
        this.posteior = posteior;
    }

    public void setPrior(double prior) {
        this.prior = prior;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public void setBeNum(int beNum) {
        BeNum = beNum;
    }

    public void setBuNum(int buNum) {
        BuNum = buNum;
    }

    public void setTempInNum(int tempInNum) {
        this.tempInNum = tempInNum;
    }

    public void setTempOutNum(int tempOutNum) {
        this.tempOutNum = tempOutNum;
    }
}