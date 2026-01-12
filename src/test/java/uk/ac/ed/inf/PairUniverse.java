package uk.ac.ed.inf;

import java.util.*;

public class PairUniverse {

    static List<String> names = List.of(
            "CARD","CVV","EXPIRY","COUNT","REST","DEF","PRICE","TOTAL","TIME"
    );

    static Map<String,List<String>> domain = Map.of(
            "CARD", List.of("VALID","SHORT", "LONG", "NON_NUMERIC"),
            "CVV", List.of("VALID","SHORT", "LONG", "NON_NUMERIC"),
            "EXPIRY", List.of("VALID","EXPIRED", "INVALID"),
            "COUNT", List.of("ONE","ZERO","FIVE"),
            "REST", List.of("SINGLE","MULTIPLE"),
            "DEF", List.of("DEFINED","UNDEFINED"),
            "PRICE", List.of("CORRECT","LOW", "HIGH"),
            "TOTAL", List.of("CORRECT","LOW","HIGH"),
            "TIME", List.of("OPEN","CLOSED")
    );

    public static Set<Pair> allPairs() {
        Set<Pair> s = new HashSet<>();
        for (int i = 0; i < names.size(); i++)
            for (int j = i+1; j < names.size(); j++)
                for (var a : domain.get(names.get(i)))
                    for (var b : domain.get(names.get(j)))

                        s.add(new Pair(names.get(i),a,names.get(j),b));
        return s;
    }

    public static Set<Pair> coveredBy(OrderTestBuilder b) {
        Map<String,String> m = b.asStringMap();
        Set<Pair> s = new HashSet<>();

        for (int i = 0; i < names.size(); i++)
            for (int j = i+1; j < names.size(); j++)
                s.add(new Pair(names.get(i), m.get(names.get(i)), names.get(j), m.get(names.get(j))));
        return s;
    }
}
