package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.inf.dataTypes.LongLat;
import uk.ac.ed.inf.dataTypes.LongLatPair;

import java.lang.Math;

@RestController
public class RESTController {

    @GetMapping("/uuid")
    public ResponseEntity<String> getUUID() {
        return ResponseEntity.ok("s2281597");
    }

    @PostMapping(value="/distanceTo", consumes="application/json")
    public ResponseEntity<Double> distanceTo(@RequestBody LongLatPair locs){
        LongLat loc1 = locs.getPos1();
        LongLat loc2 = locs.getPos2();

        if (loc1.getLong() > 0 || loc2.getLong() > 0 || loc1.getLat() < 40 || loc2.getLat() < 40) {
            return ResponseEntity.badRequest().build();
        }
        else{
            double distance = Math.sqrt(Math.pow(loc1.getLong() - loc2.getLong(), 2) + Math.pow(loc1.getLat() - loc2.getLat(), 2));

            return ResponseEntity.ok(distance);
        }
    }

}
