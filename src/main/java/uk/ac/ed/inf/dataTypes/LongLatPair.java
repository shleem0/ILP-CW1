package uk.ac.ed.inf.dataTypes;

public class LongLatPair{

    private LongLat pos1;
    private LongLat pos2;

    public LongLatPair(){}

    public LongLatPair(LongLat pos1, LongLat pos2){
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public LongLat getPosition1(){
        return pos1;
    }

    public void setPosition1(LongLat pos1){
        this.pos1 = pos1;
    }

    public LongLat getPosition2(){
        return pos2;
    }

    public void setPosition2(LongLat pos2){
        this.pos2 = pos2;
    }

}
