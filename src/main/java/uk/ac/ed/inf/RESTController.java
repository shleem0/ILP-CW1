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
import java.awt.geom.Rectangle2D;
import java.lang.Math;
import java.util.List;

import static java.lang.Double.parseDouble;
import static uk.ac.ed.inf.SystemConstants.*;

@RestController
public class RESTController {
    ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter());

    @GetMapping("/uuid")
    public ResponseEntity<String> getUUID() {
        return ResponseEntity.ok("s2281597");
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<String> getDistanceTo(@RequestBody String longLatPair) {

        LongLatPair positions;

        if (inputStringValidator(longLatPair)) { //validating if body is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try { //validating body is in correct format
            positions = mapper.readValue(longLatPair, LongLatPair.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if (positions.getPos1() == null || positions.getPos2() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {

            LongLat pos1 = positions.getPos1();
            LongLat pos2 = positions.getPos2();

            //semantic validation
            if (longLatValidator(pos1.getLng(), pos1.getLat()) || longLatValidator(pos2.getLng(), pos2.getLat())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else {
                double x = Math.pow((pos1.getLng() - pos2.getLng()), 2); //calculate distance
                double y = Math.pow((pos1.getLat() - pos2.getLat()), 2);
                double distance = Math.sqrt(x + y);

                String distanceString = String.valueOf(distance);

                return ResponseEntity.ok(distanceString);
            }
        }
    }


    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isClose(@RequestBody String longLatPair){
        //uses getDistanceTo validation
        String distance = getDistanceTo(longLatPair).getBody();

        if (inputStringValidator(distance)){ //checking if string is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        else {

            double distanceNumber = parseDouble(distance);
            boolean isClose = distanceNumber < 0.00015;

            return ResponseEntity.ok(isClose);
        }
    }


    @PostMapping("/nextPosition")
    public ResponseEntity<String> nextPosition(@RequestBody String startPosAngle) throws JsonProcessingException {

        PosAngle startPos;
        double latChange;
        double lngChange;

        if (inputStringValidator(startPosAngle)) { //checking if input string is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        else {
            try { //checking for correct JSON format
                startPos = mapper.readValue(startPosAngle, PosAngle.class);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Double angle = startPos.getAngle();
            LongLat position = startPos.getStart();

            //validating position
            if (angle == null || angle < 0 || angle > 360 || longLatValidator(position.getLng(), position.getLat())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            else{
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
    public ResponseEntity<Boolean> isInRegion(@RequestBody String posRegionStr) {

        Path2D.Double regionPoly = new Path2D.Double();
        PosRegion posRegion;

        if (posRegionStr == null || posRegionStr.isEmpty()) { //checking for empty input
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {

            try { //checking for correct JSON format
                posRegion = mapper.readValue(posRegionStr, PosRegion.class);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            LongLat position = posRegion.getPosition();
            Region region = posRegion.getRegion();
            List<LongLat> vertices = region.getVertices();

            //verifying vertices can close shape
            if (vertices.size() < 3 || !vertices.get(0).getLat().equals(vertices.get(vertices.size() - 1).getLat())
                    || !vertices.get(0).getLng().equals(vertices.get(vertices.size() - 1).getLng())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else {

                regionPoly.moveTo(vertices.get(0).getLng(), vertices.get(0).getLat()); //moving to start of polygon

                for (int i = 1; i < vertices.size(); i++) { //validating vertex positions and adding them to the region if valid
                    if (longLatValidator(vertices.get(i).getLng(), vertices.get(i).getLat())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                    } else {
                        regionPoly.lineTo(vertices.get(i).getLng(), vertices.get(i).getLat());
                    }
                }
                regionPoly.closePath();

                Double lng = position.getLng();
                Double lat = position.getLat();

                if (longLatValidator(lng, lat)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                } else {
                    boolean contains = regionPoly.contains(lng, lat); //checking if point is in polygon

                    Rectangle2D rect = new Rectangle2D.Double(lng - TOLERANCE, lat - TOLERANCE, 2 * TOLERANCE, 2 * TOLERANCE);
                    boolean onBoundary = regionPoly.intersects(rect); //checking if point is on boundary

                    boolean response = contains || onBoundary;

                    return ResponseEntity.ok(response);
                }
            }
        }
    }

    //validator methods

    public boolean longLatValidator(Double lng, Double lat){

        return lng == null || lat == null || lng < -180 || lng > 180 || lat < -90 || lat > 90;

    }

    public boolean inputStringValidator(String inputString){
        return inputString == null || inputString.isEmpty();
    }
}


