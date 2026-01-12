package uk.ac.ed.inf.dataTypes;

import java.util.List;

public class Restaurant {

    public String name;
    public LongLat location;
    public List<String> openingDays;

    public List<Pizza> menu;

    public Restaurant(){}

    public String getName(){
        return name;
    }

    public LongLat getLocation(){
        return location;
    }

    public List<Pizza> getMenu(){
        return menu;
    }

    public List<String> getDays(){
        return openingDays;
    }
}
