package uk.ac.ed.inf;

public class InputValidator{


    public boolean longLatValidator(Double lng, Double lat){
        return lng == null || lat == null || lng < -180 || lng > 180 || lat < -90 || lat > 90;
    }

    public boolean inputStringValidator(String inputString){
        return inputString == null || inputString.isEmpty();
    }
}
