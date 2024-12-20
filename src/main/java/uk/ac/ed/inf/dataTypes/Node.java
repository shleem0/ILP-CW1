package uk.ac.ed.inf.dataTypes;

import java.util.Objects;

public class Node {

    private LongLat pos;
    private double f;
    private double g;
    private Node parent;

    public LongLat getPos(){
        return pos;
    }
    public void setPos(LongLat pos){
        this.pos = pos;
    }

    public double getG(){
        return g;
    }
    public void setG(double g){
        this.g = g;
    }

    public double getF(){
        return f;
    }
    public void setF(double g, double h){
        this.f = g + h;
    }

    public Node getParent(){
        return parent;
    }
    public void setParent(Node parent){
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Node){
            Node node = (Node) obj;

            if (this.getPos().equals(node.getPos()) && this.getF() == node.getF()){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPos().getLat(), this.getPos().getLng());
    }
}
