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

    StringBuilder noFlyBody = restController.getDataFromREST("https://ilp-rest-2024.azurewebsites.net/noFlyZones");
    List<Region> noFlyZones = mapper.readValue(noFlyBody.toString(), new TypeReference<List<Region>>() {
    });

    StringBuilder centralBody = restController.getDataFromREST("https://ilp-rest-2024.azurewebsites.net/centralArea");
    Region centralArea = mapper.readValue(centralBody.toString(), Region.class);

    public PathFindingTests() throws IOException {
    }


    @Test
    public void calcDeliveryPath_invalidOrderTest() throws IOException {
        //testing getting delivery path for an invalid order

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111\"," +
                "\"creditCardExpiry\":\"05/25\",\"cvv\":\"382\"}}";

        HttpStatusCode result = restController.calcDeliveryPath(order).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }


    //testing multiple restaurants for path nodes in no fly zones
    @Test
    public void calcDeliveryPath_NoFlyZonesTest1() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/25\",\"cvv\":\"382\"}}";

        List<LongLat> path = restController.calcDeliveryPath(order).getBody();

        for (Region nfz : noFlyZones) {
            for (LongLat l : path) {

                isInRegionRequest test = new isInRegionRequest();
                test.setRegion(nfz);
                test.setPosition(l);

                assertFalse(restController.isInRegion(mapper.writeValueAsString(test)).getBody());
            }
        }
    }

    @Test
    public void calcDeliveryPath_NoFlyZonesTest2() throws IOException {
        String order = "{\"orderNo\":\"2AA463CD\",\"orderDate\":\"2025-01-08\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\"," +
                "\"priceTotalInPence\":2400," +
                "\"pizzasInOrder\":[{\"name\":\"R6: Sucuk delight\",\"priceInPence\":1400}," +
                "{\"name\":\"R6: Dreams of Syria\",\"priceInPence\":900}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111112986640930\"," +
                "\"creditCardExpiry\":\"01/26\",\"cvv\":\"318\"}}";

        List<LongLat> path = restController.calcDeliveryPath(order).getBody();

        for (Region nfz : noFlyZones) {
            for (LongLat l : path) {

                isInRegionRequest test = new isInRegionRequest();
                test.setRegion(nfz);
                test.setPosition(l);

                assertFalse(restController.isInRegion(mapper.writeValueAsString(test)).getBody());
            }
        }
    }

    @Test
    public void calcDeliveryPath_NoFlyZonesTest3() throws IOException {
        String order = "{\"orderNo\":\"5E8191FB\",\"orderDate\":\"2025-01-08\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2400," +
                "\"pizzasInOrder\":[{\"name\":\"R3: Super Cheese\",\"priceInPence\":1400}," +
                "{\"name\":\"R3: All Shrooms\",\"priceInPence\":900}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"5227438597039906\"," +
                "\"creditCardExpiry\":\"02/26\",\"cvv\":\"474\"}}";

        List<LongLat> path = restController.calcDeliveryPath(order).getBody();

        for (Region nfz : noFlyZones) {
            for (LongLat l : path) {

                isInRegionRequest test = new isInRegionRequest();
                test.setRegion(nfz);
                test.setPosition(l);

                assertFalse(restController.isInRegion(mapper.writeValueAsString(test)).getBody());
            }
        }
    }


    //testing to ensure path never enters then leaves central
    @Test
    public void calcDeliveryPath_LeavingCentralTest1() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/25\",\"cvv\":\"382\"}}";

        boolean current;
        boolean prev = false;

        List<LongLat> path = restController.calcDeliveryPath(order).getBody();

        isInRegionRequest test = new isInRegionRequest();
        test.setRegion(centralArea);

        assert path != null;
        for (LongLat l : path) {

                test.setPosition(l);
                current = restController.isInRegion(mapper.writeValueAsString(test)).getBody();

                assertTrue(!prev || current);

                prev = current;
        }
    }


    @Test
    public void calcDeliveryPath_LeavingCentralTest2() throws IOException {

        String order = "{\"orderNo\":\"2AA463CD\",\"orderDate\":\"2025-01-08\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\"," +
                "\"priceTotalInPence\":2400," +
                "\"pizzasInOrder\":[{\"name\":\"R6: Sucuk delight\",\"priceInPence\":1400}," +
                "{\"name\":\"R6: Dreams of Syria\",\"priceInPence\":900}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111112986640930\"," +
                "\"creditCardExpiry\":\"01/26\",\"cvv\":\"318\"}}";

        boolean current;
        boolean prev = false;

        List<LongLat> path = restController.calcDeliveryPath(order).getBody();

        isInRegionRequest test = new isInRegionRequest();
        test.setRegion(centralArea);

        assert path != null;
        for (LongLat l : path) {

            test.setPosition(l);
            current = restController.isInRegion(mapper.writeValueAsString(test)).getBody();

            assertTrue(!prev || current);

            prev = current;
        }
    }


    @Test
    public void calcDeliveryPath_LeavingCentralTest3() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/25\",\"cvv\":\"382\"}}";

        boolean current;
        boolean prev = false;

        List<LongLat> path = restController.calcDeliveryPath(order).getBody();

        isInRegionRequest test = new isInRegionRequest();
        test.setRegion(centralArea);

        assert path != null;
        for (LongLat l : path) {

            test.setPosition(l);
            current = restController.isInRegion(mapper.writeValueAsString(test)).getBody();

            assertTrue(!prev || current);

            prev = current;
        }
    }
}
