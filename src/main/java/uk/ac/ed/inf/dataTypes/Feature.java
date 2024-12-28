package uk.ac.ed.inf.dataTypes;

public class Feature {
    private final String type = "Feature";
    private Geometry geometry;
    private Property properties;

    public Feature(Geometry geometry, Property properties){
        this.geometry = geometry;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Geometry getGeometry() {
        return geometry;
    }
    public Property getProperties() {
        return properties;
    }

    public void setProperties(Property properties){
        this.properties = properties;
    }

    public void setGeometry(Geometry geometry){
        this.geometry = geometry;
    }
}
