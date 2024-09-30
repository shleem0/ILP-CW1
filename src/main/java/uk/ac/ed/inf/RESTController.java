package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.inf.dataTypes.*;

import java.awt.geom.Path2D;
import java.lang.Math;
import java.util.Arrays;
import java.util.List;

import static java.lang.Double.parseDouble;
import static uk.ac.ed.inf.SystemConstants.DF;
import static uk.ac.ed.inf.SystemConstants.MOVEMENT;

@RestController
public class RESTController {
    ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter());

    @GetMapping("/uuid")
    public ResponseEntity<String> getUUID() {
        return ResponseEntity.ok("s2281597");
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<String> getDistanceTo(@RequestBody String longLatPair){

        LongLatPair positions;

        if (longLatPair.isEmpty()){ //validating if body is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        try { //validating body is in correct format
            positions = mapper.readValue(longLatPair, LongLatPair.class);
        }
        catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        LongLat pos1 = positions.getPos1();
        LongLat pos2 = positions.getPos2();

        //semantic validation
        if (pos1 == null || pos2 == null || pos1.getLng() > 180 || pos2.getLng() > 180 || pos1.getLng() < -180 ||
        pos2.getLng() < -180 || pos1.getLat() < -90 || pos2.getLat() < -90 || pos1.getLat() > 90 || pos2.getLat() > 90) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } else {
            double x = Math.pow((pos1.getLng() - pos2.getLng()), 2); //calculate distance
            double y = Math.pow((pos1.getLat() - pos2.getLat()), 2);
            double distance = Math.sqrt(x + y);

            String distanceString = String.valueOf(distance);

            return ResponseEntity.ok(distanceString);
        }
    }


    @PostMapping("/isClose")
    public ResponseEntity<Boolean> isClose(@RequestBody String longLatPair){
        //uses getDistanceTo validation
        String distance = getDistanceTo(longLatPair).getBody();

        if (distance.isEmpty() || distance.equals("")){ //checking if string is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        else {

            double distanceNumber = parseDouble(distance);

            if (distanceNumber < 0.00015) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        }
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<String> nextPosition(@RequestBody String startPosAngle) throws JsonProcessingException {

        PosAngle startPos;
        double latChange;
        double lngChange;
        Double[] directions = new Double[]{0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5, 180.0, 202.5, 225.0, 247.5,
                                            270.0, 292.5, 315.0, 337.5, 999.0};

        if (startPosAngle.isEmpty()) { //checking if input string is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        else {
            try { //checking for correct JSON format
                startPos = mapper.readValue(startPosAngle, PosAngle.class);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            double angle = startPos.getAngle();
            LongLat position = startPos.getStart();

            //validating position
            if (!Arrays.asList(directions).contains(angle) || position.getLng() > 180 || position.getLng() < -180 ||
            position.getLat() > 90 || position.getLat() < -90){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            else if (angle != 999.0) {
                latChange = MOVEMENT * Math.cos(Math.toRadians(angle)); //calculating movement in lat and long
                lngChange = MOVEMENT * Math.sin(Math.toRadians(angle));

                //adjusting position (and formatting)
                position.setLat(Double.parseDouble(DF.format(position.getLat() + latChange)));
                position.setLng(Double.parseDouble(DF.format(position.getLng() + lngChange)));
            }

            String nextPosition = mapper.writeValueAsString(position);
            return ResponseEntity.ok(nextPosition);
        }
    }


    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody String posRegionStr){

        Path2D.Double regionPoly = new Path2D.Double();
        PosRegion posRegion;

        if (posRegionStr.isEmpty()) { //checking for empty input
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        else{

            try{ //checking for correct JSON format
                posRegion = mapper.readValue(posRegionStr, PosRegion.class);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            LongLat position = posRegion.getPosition();
            Region region = posRegion.getRegion();
            List<LongLat> vertices = region.getVertices();

            if(vertices.size() < 3){ //verifying vertices can close shape
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            regionPoly.moveTo(vertices.get(0).getLng(), vertices.get(0).getLat()); //moving to start of polygon

            for (int i = 1; i < vertices.size(); i++) { //validating vertex positions and adding them to the region if valid
                if (vertices.get(i).getLng() > 180 || vertices.get(i).getLng() < -180 || vertices.get(i).getLat() < -90
                        || vertices.get(i).getLat() > 90) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                else {
                    regionPoly.lineTo(vertices.get(i).getLng(), vertices.get(i).getLat());
                }
            }
            regionPoly.closePath();

            double lng = position.getLng();
            double lat = position.getLat();

            boolean response = regionPoly.contains(lng, lat);
            return ResponseEntity.ok(response);

        }
    }
}


