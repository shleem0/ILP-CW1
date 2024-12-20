package uk.ac.ed.inf.dataTypes;

public class LongLat{

    public Double lng;
    public Double lat;


    public Double getLng(){
        return this.lng;
    }

    public Double getLat(){
        return this.lat;
    }

    public void setLng(Double lng){
        this.lng = lng;
    }

    public void setLat(Double lat){
        this.lat = lat;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof LongLat){

            if (this.lng.equals(((LongLat)obj).lng) && this.lat.equals(((LongLat) obj).lat)){
                return true;
            }
        }
        return false;
    }
}
