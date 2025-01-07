package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import uk.ac.ed.inf.dataTypes.LongLat;
import uk.ac.ed.inf.dataTypes.Region;
import uk.ac.ed.inf.dataTypes.isInRegionRequest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PathFindingTests {

    RESTController restController = new RESTController();
    ObjectMapper mapper = new ObjectMapper();


    @Test
    public void calcDeliveryPath_invalidOrderTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111\"," +
                "\"creditCardExpiry\":\"05/25\",\"cvv\":\"382\"}}";

        HttpStatusCode result = restController.calcDeliveryPath(order).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }


    @Test
    public void calcDeliveryPath_NoFlyZonesTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/25\",\"cvv\":\"382\"}}";

        StringBuilder noFlyBody = restController.getDataFromREST("https://ilp-rest-2024.azurewebsites.net/noFlyZones");
        List<Region> noFlyZones = mapper.readValue(noFlyBody.toString(), new TypeReference<List<Region>>() {});

        List<LongLat> path = restController.calcDeliveryPath(order).getBody();

        for (Region nfz : noFlyZones){
            for (LongLat l : path){

                isInRegionRequest test = new isInRegionRequest();
                test.setRegion(nfz);
                test.setPosition(l);

                assertFalse(restController.isInRegion(mapper.writeValueAsString(test)).getBody());
            }
        }
    }
}
