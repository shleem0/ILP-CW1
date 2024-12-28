package uk.ac.ed.inf.dataTypes;

import java.util.List;

public class Region {

    private String name;
    private List<LongLat> vertices;

    public String getName(){return name;}

    public void setName(String name){
        this.name = name;}

    public List<LongLat> getVertices(){
        return vertices;
    }

    public void setVertices(List<LongLat> vertices){
        this.vertices = vertices;
    }
}
