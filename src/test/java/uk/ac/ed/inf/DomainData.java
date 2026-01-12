package uk.ac.ed.inf;

import java.util.Map;

public class DomainData {

    public static final Map<String,Integer> PIZZAS = Map.of(
            "R1: Margarita", 1000,
            "R1: Calzone", 1400,
            "R2: Meat Lover", 1400,
            "R2: Vegan Delight", 1100
    );

    public static final Map<String,String[]> RESTAURANTS = Map.of(
            "R1", new String[]{"R1: Margarita","R1: Calzone"},
            "R2", new String[]{"R2: Meat Lover", "R2: Vegan Delight"}
    );
}
