package uk.ac.ed.inf.dataTypes;

public class Pizza {

    public String name;
    public int priceInPence;

    public String getName(){
        return name;
    }

    public int getPrice(){
        return priceInPence;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if (obj == null || getClass() != obj.getClass()){
            return false;
        }
        Pizza other = (Pizza) obj;
        return !name.isEmpty() && name.equals(other.name) && priceInPence == other.priceInPence;
    }

    @Override
    public int hashCode(){
        return !name.isEmpty() ? name.hashCode() : 0;
    }
}
