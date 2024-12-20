package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemConstants {

    public static final double MOVEMENT = 0.00015;
    public static final double TOLERANCE = 0.0000001;

    public static final Double APPLETON_LNG = -3.186874;
    public static final Double APPLETON_LAT = 55.944494;

    public static final List<Double> ANGLES = new ArrayList<>(Arrays.asList(0.0, 22.5, 45.0, 67.5, 90.0, 112.5,
            135.0, 157.5, 180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5, 999.0));
}
