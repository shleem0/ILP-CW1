package uk.ac.ed.inf.dataTypes;

public class LongLat{

    private double lat;
    private double lon;

    public LongLat(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLong(){
        return this.lon;
    }

    public double getLat(){
        return this.lat;
    }
}
