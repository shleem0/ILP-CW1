package uk.ac.ed.inf;

import uk.ac.ed.inf.dataTypes.LongLat;

public class InputValidator{


    public boolean longLatValidator(Double lng, Double lat){
        return lng == null || lat == null || lng < -180 || lng > 180 || lat < -90 || lat > 90;
    }



    public void longLatConverter(LongLat ll){

        Double newPos;

        if (ll.getLng() > 180) {
            newPos = ll.getLng() - 180.0;
            ll.setLng(-180 + newPos);
        }
        else if (ll.getLng() < -180){
            newPos = ll.getLng() + 180.0;
            ll.setLng(180 + newPos);
        }

        if (ll.getLat() > 90){
            newPos = ll.getLat() - 90.0;
            ll.setLat(-90 + newPos);
        }
        else if (ll.getLat() < -90){
            newPos = ll.getLat() + 90.0;
            ll.setLat(90 + newPos);
        }
    }



    public boolean inputStringValidator(String inputString){
        return inputString == null || inputString.isEmpty();
    }
}
