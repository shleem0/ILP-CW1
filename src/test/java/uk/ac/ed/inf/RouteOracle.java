package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.dataTypes.LongLat;
import uk.ac.ed.inf.dataTypes.LongLatPair;
import uk.ac.ed.inf.dataTypes.Region;
import uk.ac.ed.inf.dataTypes.isInRegionRequest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Math.*;
import static uk.ac.ed.inf.SystemConstants.*;

public final class RouteOracle {

    public static List<String> validate(List<LongLat> path,
                                        Region central,
                                        List<Region> noFly,
                                        LongLat restaurant) throws JsonProcessingException {

        List<String> errors = new ArrayList<>();
        RESTController rc = new RESTController();
        ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        LongLat tower = new LongLat(APPLETON_LNG, APPLETON_LAT);

        // R1.2.5 Hover
        if (path.get(0) != path.get(1)) errors.add("NO_START_HOVER");
        if (path.get(path.size()-1) != path.get(path.size()-2)) errors.add("NO_END_HOVER");

        // R1.2.4 start/end
        if (!path.get(0).equals(restaurant)) errors.add("BAD_START");

        LongLatPair compare = new LongLatPair(path.get(path.size()-1), tower);
        if (parseDouble(rc.getDistanceTo(mapper.writeValueAsString(compare)).getBody()) > 0.00015)
            errors.add("BAD_END");

        boolean enteredCentral = false;

        for (int i=1;i<path.size();i++) {

            LongLat a = path.get(i-1);
            LongLat b = path.get(i);

            LongLatPair ab = new LongLatPair(a, b);
            double distance = parseDouble(rc.getDistanceTo(mapper.writeValueAsString(ab)).getBody());

            DecimalFormat df = new DecimalFormat("#.#");
            double angle = toDegrees(atan2(b.getLat() - a.getLat(), b.getLng() - a.getLng()));
            angle = abs(parseDouble(df.format(angle)));


            // R1.2.1 step geometry
            if (Math.abs(distance - 0.00015) > 1e-7 && distance != 0)
                errors.add("BAD_STEP");

            // R1.2.1 direction
            if (!ANGLES.contains(angle)) errors.add("BAD_DIRECTION");

            // R1.2.2 no-fly
            isInRegionRequest a_req = new isInRegionRequest();
            a_req.setPosition(a);

            isInRegionRequest b_req = new isInRegionRequest();
            b_req.setPosition(b);

            for (var nf : noFly) {
                a_req.setRegion(nf);
                b_req.setRegion(nf);

                if (rc.isInRegion(mapper.writeValueAsString(a_req)).getBody() || rc.isInRegion(mapper.writeValueAsString(b_req)).getBody())
                    errors.add("NO_FLY_VIOLATION");
            }

            // R1.2.3 central area
            a_req.setRegion(central);
            b_req.setRegion(central);

            if (rc.isInRegion(mapper.writeValueAsString(a_req)).getBody()) enteredCentral = true;
            else if (enteredCentral)
                errors.add("LEFT_CENTRAL");
        }

        return errors;
    }
}
