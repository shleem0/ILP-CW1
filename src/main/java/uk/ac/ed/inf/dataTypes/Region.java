package uk.ac.ed.inf.dataTypes;

import java.util.List;

public class Region {

    public String name;
    public List<LongLat> vertices;

    public String getName(){
        return name;
    }

    public List<LongLat> getVertices(){
        return vertices;
    }
}
