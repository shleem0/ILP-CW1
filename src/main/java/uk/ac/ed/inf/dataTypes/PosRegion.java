package uk.ac.ed.inf.dataTypes;

public class PosRegion {

    public LongLat position;
    public Region region;

    public LongLat getPosition(){
        return position;
    }

    public void setPosition(LongLat position){
        this.position = position;
    }

    public Region getRegion(){
        return region;
    }
    public void setRegion(Region region){
        this.region = region;
    }
}
