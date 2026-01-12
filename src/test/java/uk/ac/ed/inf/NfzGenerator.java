package uk.ac.ed.inf;

import java.util.Random;

import static uk.ac.ed.inf.SystemConstants.MOVEMENT;

public final class NfzGenerator {

    public static String generateCorridors(int count) {

        double baseLng = -3.1890;
        double baseLat = 55.9445;
        double delta = 0.00035;

        StringBuilder sb = new StringBuilder("[\n");

        for (int i = 0; i < count; i++) {
            double shift = i * delta;

            sb.append("""
            {
              "name":"NFZ_%d",
              "vertices":[
                {"lng":%.6f,"lat":%.6f},
                {"lng":%.6f,"lat":%.6f},
                {"lng":%.6f,"lat":%.6f},
                {"lng":%.6f,"lat":%.6f},
                {"lng":%.6f,"lat":%.6f}
              ]
            },
            """.formatted(i,
                    baseLng + shift, baseLat,
                    baseLng + shift + 0.0006, baseLat,
                    baseLng + shift + 0.0006, baseLat + 0.0002,
                    baseLng + shift, baseLat + 0.0002,
                    baseLng + shift, baseLat));
        }

        sb.setLength(sb.length() - 2);
        sb.append("\n]");
        return sb.toString();
    }


    public static String generateRadial(int count, double radius) {
        StringBuilder sb = new StringBuilder("[");
        double cx = -3.186874, cy = 55.944494;

        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double dx = Math.cos(angle) * radius;
            double dy = Math.sin(angle) * radius;

            sb.append("""
        {
          "name":"R%s",
          "vertices":[
            {"lng":%f,"lat":%f},
            {"lng":%f,"lat":%f},
            {"lng":%f,"lat":%f},
            {"lng":%f,"lat":%f},
            {"lng":%f,"lat":%f}
          ]
        },
        """.formatted(i,
                    cx+dx, cy+dy,
                    cx+dx+0.0001, cy+dy,
                    cx+dx+0.0001, cy+dy+0.0001,
                    cx+dx, cy+dy+0.0001,
                    cx+dx, cy+dy
            ));
        }
        sb.replace(sb.length() - 2, sb.length() - 1, "]");
        return sb.toString();
    }


    public static String generateMaze(int rows, int cols, double spacing) {
        StringBuilder sb = new StringBuilder("[");
        double baseX = -3.191, baseY = 55.944;

        int id = 0;
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                double x = baseX + c*spacing;
                double y = baseY + r*spacing;
                sb.append("""
            {
              "name":"M%s",
              "vertices":[
                {"lng":%f,"lat":%f},
                {"lng":%f,"lat":%f},
                {"lng":%f,"lat":%f},
                {"lng":%f,"lat":%f},
                {"lng":%f,"lat":%f}
              ]
            },
            """.formatted(id++, x,y, x+0.00012,y, x+0.00012,y+0.00012, x,y+0.00012, x,y));
            }
        }
        sb.replace(sb.length() - 2, sb.length() - 1, "]");
        return sb.toString();
    }


    public static String generateScatter(int count, long seed) {
        Random r = new Random(seed);
        StringBuilder sb = new StringBuilder("[");
        for (int i=0;i<count;i++) {
            double x = -3.192 + r.nextDouble()*0.006;
            double y = 55.943 + r.nextDouble()*0.006;
            sb.append(generateSquare("S"+i, x, y, 0.00012));
        }
        sb.replace(sb.length() - 2, sb.length() - 1, "]");
        return sb.toString();
    }

    public static String generateWall(int count) {
        StringBuilder sb = new StringBuilder("[");
        double x = -3.1888;
        for (int i=0;i<count;i++) {
            double y = 55.9435 + i*0.00015;
            sb.append(generateSquare("W"+i, x, y, MOVEMENT * 1.5));
        }
        sb.replace(sb.length() - 2, sb.length() - 1, "]");
        return sb.toString();
    }


    private static String generateSquare(String name,double x,double y,double s){
        return """
    {
      "name":"%s",
      "vertices":[
        {"lng":%f,"lat":%f},
        {"lng":%f,"lat":%f},
        {"lng":%f,"lat":%f},
        {"lng":%f,"lat":%f},
        {"lng":%f,"lat":%f}
      ]
    },
    """.formatted(name,x,y,x+s,y,x+s,y+s,x,y+s,x,y);
    }



}
