package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.inf.dataTypes.LongLat;
import uk.ac.ed.inf.dataTypes.LongLatPair;
import java.lang.Math;
import java.util.List;

@RestController
public class RESTController {
    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/uuid")
    public ResponseEntity<String> getUUID() {
        return ResponseEntity.ok("s2281597");
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<String> getDistanceTo(@RequestBody String longLatPair) throws JsonProcessingException {

        LongLatPair positions;

        try {
            positions = mapper.readValue(longLatPair, LongLatPair.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid coordinates");
        }

        LongLat pos1 = positions.getPos1();
        LongLat pos2 = positions.getPos2();

        if (pos1 == null || pos2 == null || pos1.getLong() > 0 || pos2.getLong() > 0 || pos1.getLat() < 35 || pos2.getLat() < 35) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid coordinates");
        } else {
            double x = Math.pow((pos1.getLong() - pos2.getLong()), 2);
            double y = Math.pow((pos1.getLat() - pos2.getLat()), 2);
            double distance = Math.sqrt(x + y);

            return ResponseEntity.ok(distance + "");
        }
    }
}

