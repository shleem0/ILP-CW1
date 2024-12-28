package uk.ac.ed.inf.dataTypes;

public class MoveRequest{

    private LongLat start;
    private Double angle;

    public LongLat getStart(){return start;}

    public void setStart(LongLat start){
        this.start = start;
    }

    public Double getAngle(){return angle;}

    public void setAngle(Double angle){
        this.angle = angle;
    }
}
