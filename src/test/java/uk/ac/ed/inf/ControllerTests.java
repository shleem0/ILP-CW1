package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.ed.inf.SystemConstants.ANGLES;
import static uk.ac.ed.inf.SystemConstants.MOVEMENT;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import uk.ac.ed.inf.dataTypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ControllerTests {

    RESTController restController = new RESTController();
    ObjectMapper mapper = new ObjectMapper();
    Random r = new Random();

    //getUUID
    @Test
    public void getUUIDTest(){
        String result = restController.getUUID().getBody();
        String uuid = "s2281597";

        assertEquals(uuid, result);
    }



    //getDistanceTo
    @Test
    public void getDistanceTo_ValidInputTest() throws JsonProcessingException {
        //checks getDistance for valid input

        LongLatPair testPair = generateRandomLongLatPair();

        double lng1 = testPair.getPosition1().getLng();
        double lat1 = testPair.getPosition1().getLat();

        double lng2 = testPair.getPosition2().getLng();
        double lat2 = testPair.getPosition2().getLat();

        Double result = Double.valueOf(restController.getDistanceTo(mapper.writeValueAsString(testPair)).getBody());
        Double expected = Math.sqrt(Math.pow(lng1 - lng2, 2) + Math.pow(lat1 - lat2, 2));

        assertEquals(expected, result);
    }

    @Test
    public void getDistanceTo_SamePosTest() throws JsonProcessingException {
        //checks for 2 identical longlats

        LongLatPair testPair = new LongLatPair();
        LongLat pos1 = new LongLat();
        LongLat pos2 = new LongLat();

        pos1.setLat(0.0);
        pos2.setLat(0.0);

        pos1.setLng(0.0);
        pos2.setLng(0.0);

        testPair.setPosition1(pos1);
        testPair.setPosition2(pos2);

        Double result = Double.valueOf(restController.getDistanceTo(mapper.writeValueAsString(testPair)).getBody());

        assertEquals(0.0, result);
    }

    @Test
    public void getDistanceTo_EmptyObjectTest() throws JsonProcessingException {
        //checks for an LLP object with null attributes

        LongLatPair testPair = new LongLatPair();

        HttpStatusCode result = restController.getDistanceTo(mapper.writeValueAsString(testPair)).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void getDistanceTo_EmptyStringTest(){
        //checks for an empty input string
        HttpStatusCode result = restController.getDistanceTo("").getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void getDistanceTo_MissingPosTest() throws JsonProcessingException {
        //checks for an LLP object with one null position attribute

        LongLatPair testPair = new LongLatPair();
        LongLat pos1 = generateRandomLongLat();

        testPair.setPosition1(pos1);

        HttpStatusCode result = restController.getDistanceTo(mapper.writeValueAsString(testPair)).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }



    //isClose
    @Test
    public void isCloseTest() throws JsonProcessingException {
        //checks result = expected (validation is checked by getDistance tests)

        LongLatPair testPair = generateRandomLongLatPair();

        double lng1 = testPair.getPosition1().getLng();
        double lat1 = testPair.getPosition1().getLat();

        double lng2 = testPair.getPosition2().getLng();
        double lat2 = testPair.getPosition2().getLat();

        boolean result = restController.isClose(mapper.writeValueAsString(testPair)).getBody();

        double distance = Math.sqrt(Math.pow(lng1 - lng2, 2) + Math.pow(lat1 - lat2, 2));
        boolean expected = distance < MOVEMENT;

        assertEquals(expected, result);
    }




    //nextPosition
    @Test
    public void nextPosition_ValidInputTest() throws JsonProcessingException {
        //checks nextPosition for a valid input

        MoveRequest testMove = generateRandomMoveRequest();
        Double angle = testMove.getAngle();
        LongLat start = testMove.getStart();

        String result = restController.nextPosition(mapper.writeValueAsString(testMove)).getBody();
        LongLat endPosition = mapper.readValue(result, LongLat.class);

        Double expEndLng = start.getLng() + MOVEMENT * Math.sin(Math.toRadians(angle));
        Double expEndLat = start.getLat() + MOVEMENT * Math.cos(Math.toRadians(angle));

        assertEquals(expEndLng, endPosition.getLng());
        assertEquals(expEndLat, endPosition.getLat());
    }

    @Test
    public void nextPosition_EmptyInputTest() throws JsonProcessingException {
        //checks nextPosition with an empty string input

        HttpStatusCode result = restController.nextPosition("").getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPosition_MissingPosTest() throws JsonProcessingException {

        MoveRequest testMove = generateRandomMoveRequest();
        testMove.setStart(null);

        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(testMove)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPosition_MissingAngleTest() throws JsonProcessingException {

        MoveRequest testMove = generateRandomMoveRequest();
        testMove.setAngle(null);

        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(testMove)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPosition_InvalidAngleTest1() throws JsonProcessingException {

        MoveRequest testMove = generateRandomMoveRequest();
        testMove.setAngle(-5.0);

        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(testMove)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPosition_InvalidAngleTest2() throws JsonProcessingException {

        MoveRequest testMove = generateRandomMoveRequest();
        testMove.setAngle(370.0);

        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(testMove)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPosition_InvalidAngleTest3() throws JsonProcessingException {

        MoveRequest testMove = generateRandomMoveRequest();

        Double randomAngle = null;

        while (ANGLES.contains(randomAngle)){

            randomAngle = r.nextDouble(360);
        }

        testMove.setAngle(randomAngle);

        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(testMove)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPosition_EdgeAngleTest1() throws JsonProcessingException {

        MoveRequest testMove = generateRandomMoveRequest();
        testMove.setAngle(360.0);

        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(testMove)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPosition_EdgeAngleTest2() throws JsonProcessingException {

        MoveRequest testMove = generateRandomMoveRequest();
        testMove.setAngle(0.0);

        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(testMove)).getStatusCode();
        assertEquals(HttpStatus.OK, result);
    }

    @Test
    public void nextPosition_InvalidEndPosTest() throws JsonProcessingException {

        MoveRequest testMove = new MoveRequest();
        LongLat edge = new LongLat();
        edge.setLat(90.0);
        edge.setLng(180.0);

        testMove.setStart(edge);
        testMove.setAngle(0.0);

        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(testMove)).getStatusCode();
        assertEquals(HttpStatus.OK, result);
    }



    //isInRegion
    @Test
    public void isInRegion_inRegionTest() throws JsonProcessingException {

        List<LongLat> vertices = new ArrayList<>();

        vertices.add(createVertex(10.0, 10.0));
        vertices.add(createVertex(10.0, -10.0));
        vertices.add(createVertex(-10.0, 10.0));
        vertices.add(createVertex(-10.0, -10.0));
        vertices.add(createVertex(10.0, 10.0));

        LongLat pos = createVertex(0.0, 0.0);

        isInRegionRequest testRegionReq = new isInRegionRequest();
        testRegionReq.setPosition(pos);

        Region testRegion = new Region();
        testRegion.setVertices(vertices);
        testRegion.setName("test");
        testRegionReq.setRegion(testRegion);


        boolean result = restController.isInRegion(mapper.writeValueAsString(testRegionReq)).getBody();

        assertTrue(result);
    }

    @Test
    public void isInRegion_outOfRegionTest() throws JsonProcessingException {

        List<LongLat> vertices = new ArrayList<>();

        vertices.add(createVertex(10.0, 10.0));
        vertices.add(createVertex(10.0, -10.0));
        vertices.add(createVertex(-10.0, 10.0));
        vertices.add(createVertex(-10.0, -10.0));
        vertices.add(createVertex(10.0, 10.0));

        LongLat pos = createVertex(20.0, 20.0);

        isInRegionRequest testRegionReq = new isInRegionRequest();
        testRegionReq.setPosition(pos);

        Region testRegion = new Region();
        testRegion.setVertices(vertices);
        testRegion.setName("test");
        testRegionReq.setRegion(testRegion);


        boolean result = restController.isInRegion(mapper.writeValueAsString(testRegionReq)).getBody();

        assertFalse(result);
    }

    @Test
    public void isInRegion_onBoundaryTest() throws JsonProcessingException {

        List<LongLat> vertices = new ArrayList<>();

        vertices.add(createVertex(10.0, 10.0));
        vertices.add(createVertex(10.0, -10.0));
        vertices.add(createVertex(-10.0, 10.0));
        vertices.add(createVertex(-10.0, -10.0));
        vertices.add(createVertex(10.0, 10.0));

        LongLat pos = createVertex(10.0, 5.0);

        isInRegionRequest testRegionReq = new isInRegionRequest();
        testRegionReq.setPosition(pos);

        Region testRegion = new Region();
        testRegion.setVertices(vertices);
        testRegion.setName("test");
        testRegionReq.setRegion(testRegion);


        boolean result = restController.isInRegion(mapper.writeValueAsString(testRegionReq)).getBody();

        assertTrue(result);
    }

    @Test
    public void isInRegion_onVertexTest() throws JsonProcessingException {

        List<LongLat> vertices = new ArrayList<>();

        vertices.add(createVertex(10.0, 10.0));
        vertices.add(createVertex(10.0, -10.0));
        vertices.add(createVertex(-10.0, 10.0));
        vertices.add(createVertex(-10.0, -10.0));
        vertices.add(createVertex(10.0, 10.0));

        LongLat pos = createVertex(10.0, 10.0);

        isInRegionRequest testRegionReq = new isInRegionRequest();
        testRegionReq.setPosition(pos);

        Region testRegion = new Region();
        testRegion.setVertices(vertices);
        testRegion.setName("test");
        testRegionReq.setRegion(testRegion);


        boolean result = restController.isInRegion(mapper.writeValueAsString(testRegionReq)).getBody();

        assertTrue(result);
    }

    @Test
    public void isInRegion_regionNotClosedTest() throws JsonProcessingException {

        List<LongLat> vertices = new ArrayList<>();

        vertices.add(createVertex(10.0, 10.0));
        vertices.add(createVertex(10.0, -10.0));
        vertices.add(createVertex(-10.0, 10.0));
        vertices.add(createVertex(-10.0, -10.0));

        LongLat pos = createVertex(10.0, 10.0);

        isInRegionRequest testRegionReq = new isInRegionRequest();
        testRegionReq.setPosition(pos);

        Region testRegion = new Region();
        testRegion.setVertices(vertices);
        testRegion.setName("test");
        testRegionReq.setRegion(testRegion);


        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(testRegionReq)).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void isInRegion_tooFewVerticesTest() throws JsonProcessingException {

        List<LongLat> vertices = new ArrayList<>();

        vertices.add(createVertex(10.0, 10.0));
        vertices.add(createVertex(10.0, -10.0));
        vertices.add(createVertex(10.0, 10.0));

        LongLat pos = createVertex(10.0, 10.0);

        isInRegionRequest testRegionReq = new isInRegionRequest();
        testRegionReq.setPosition(pos);

        Region testRegion = new Region();
        testRegion.setVertices(vertices);
        testRegion.setName("test");
        testRegionReq.setRegion(testRegion);


        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(testRegionReq)).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }





//generating test objects
    public LongLat generateRandomLongLat(){
        Double lng = r.nextDouble(180-(-180)) - 180;
        Double lat = r.nextDouble(90-(-90)) - 90;

        LongLat longlat = new LongLat();
        longlat.setLng(lng);
        longlat.setLat(lat);

        return longlat;
    }

    public LongLatPair generateRandomLongLatPair(){
        LongLat l1 = generateRandomLongLat();
        LongLat l2 = generateRandomLongLat();

        LongLatPair llp = new LongLatPair();
        llp.setPosition1(l1);
        llp.setPosition2(l2);

        return llp;
    }

    public MoveRequest generateRandomMoveRequest(){
        LongLat start = generateRandomLongLat();
        Double angle = ANGLES.get(r.nextInt(ANGLES.size()));

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setStart(start);
        moveRequest.setAngle(angle);

        return moveRequest;
    }

    public LongLat createVertex(Double lng, Double lat){
        LongLat vertex = new LongLat();

        vertex.setLng(lng);
        vertex.setLat(lat);

        return vertex;
    }
}
