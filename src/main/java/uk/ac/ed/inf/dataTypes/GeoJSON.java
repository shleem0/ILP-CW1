package uk.ac.ed.inf.dataTypes;

import java.util.List;

public class GeoJSON {
    private final String type = "FeatureCollection";
    private List<Feature> features;

    public String getType(){
        return type;
    }

    public List<Feature> getFeatures(){
        return features;
    }

    public void setFeatures(List<Feature> features){
        this.features = features;
    }
}
