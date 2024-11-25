package uk.ac.ed.inf.dataTypes;

import java.util.List;

public class Restaurant {

    public String name;
    public LongLat location;
    public List<String> openingDays;

    public List<Pizza> menu;

    public String getName(){
        return name;
    }

    public List<Pizza> getMenu(){
        return menu;
    }

    public List<String> getDays(){
        return openingDays;
    }
}
