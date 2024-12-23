package uk.ac.ed.inf.dataTypes;

import java.util.List;

public class Geometry {
    private final String type = "LineString";
    private List<List<Double>> coordinates;

    public String getType(){
        return type;
    }

    public List<List<Double>> getCoordinates(){
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates){
        this.coordinates = coordinates;
    }
}
