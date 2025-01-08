package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.net.URL;
import java.net.HttpURLConnection;

import static java.lang.Double.parseDouble;
import static uk.ac.ed.inf.SystemConstants.*;

@RestController
public class RESTController {
    ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter());
    InputValidator validator = new InputValidator();



    @GetMapping("/uuid")
    public ResponseEntity<String> getUUID() {
        return ResponseEntity.ok("s2281597");
    }



    @PostMapping("/distanceTo")
    public ResponseEntity<String> getDistanceTo(@RequestBody String longLatPair) {

        LongLatPair positions;

        if (validator.inputStringValidator(longLatPair)) { //checking if body is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
        }

        try { //validating body is in correct format
            positions = mapper.readValue(longLatPair, LongLatPair.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot map JSON to object");
        }

        if (positions.getPosition1() == null || positions.getPosition2() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing position(s)");
        } else {

            LongLat pos1 = positions.getPosition1();
            LongLat pos2 = positions.getPosition2();

            //semantic validation
            if (validator.longLatValidator(pos1.getLng(), pos1.getLat()) || validator.longLatValidator(pos2.getLng(), pos2.getLat())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Semantically invalid");
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

        if (validator.inputStringValidator(distance)){ //checking if string is empty
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

        MoveRequest startPos;
        double latChange;
        double lngChange;

        if (validator.inputStringValidator(startPosAngle)) { //checking if input string is empty
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input string is empty");
        }
        else {
            try { //checking for correct JSON format
                startPos = mapper.readValue(startPosAngle, MoveRequest.class);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot map JSON to object");
            }

            Double angle = startPos.getAngle();
            LongLat position = startPos.getStart();

            //validating position
            if (angle == null || position == null || angle < 0 || (angle >= 360 && angle != 999) || validator.longLatValidator(position.getLng(), position.getLat())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid position");
            }
            else{

                //hover
                if (angle == 999.0){
                    return ResponseEntity.ok(mapper.writeValueAsString(position));
                }
                latChange = MOVEMENT * Math.cos(Math.toRadians(angle)); //calculating movement in lat and long
                lngChange = MOVEMENT * Math.sin(Math.toRadians(angle));

                //adjusting position
                position.setLat(position.getLat() + latChange);
                position.setLng(position.getLng() + lngChange);
            }

            while (validator.longLatValidator(position.getLng(), position.getLat())) {
                validator.longLatConverter(position);
            }

            String nextPosition = mapper.writeValueAsString(position);
            return ResponseEntity.ok(nextPosition);
        }
    }



    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody String posRegionStr) {

        Path2D.Double regionPoly = new Path2D.Double();
        isInRegionRequest posRegion;

        if (validator.inputStringValidator(posRegionStr)) { //checking for empty input
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {

            try { //checking for correct JSON format
                posRegion = mapper.readValue(posRegionStr, isInRegionRequest.class);
            } catch (JsonProcessingException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            LongLat position = posRegion.getPosition();
            Region region = posRegion.getRegion();
            List<LongLat> vertices = region.getVertices();

            //verifying vertices can close shape
            if (vertices.size() <= 3 || !vertices.get(0).getLat().equals(vertices.get(vertices.size() - 1).getLat())
                    || !vertices.get(0).getLng().equals(vertices.get(vertices.size() - 1).getLng())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else {

                regionPoly.moveTo(vertices.get(0).getLng(), vertices.get(0).getLat()); //moving to start of polygon

                for (int i = 1; i < vertices.size(); i++) { //validating vertex positions and adding them to the region if valid
                    if (validator.longLatValidator(vertices.get(i).getLng(), vertices.get(i).getLat())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                    } else {
                        regionPoly.lineTo(vertices.get(i).getLng(), vertices.get(i).getLat());
                    }
                }
                regionPoly.closePath();

                Double lng = position.getLng();
                Double lat = position.getLat();

                if (validator.longLatValidator(lng, lat)){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }

                boolean contains = regionPoly.contains(lng, lat);
                boolean onBoundary = getOnBoundary(position, regionPoly);

                boolean response = contains || onBoundary;

                return ResponseEntity.ok(response);
                }
            }
        }



    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationResult> validateOrder(@RequestBody String orderStr) throws IOException {

        //initialise local variables
        Order order;
        OrderValidationResult result = new OrderValidationResult(OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED);

        //get restaurants from rest service
        StringBuilder resBody = getDataFromREST("https://ilp-rest-2024.azurewebsites.net/restaurants");
        List<Restaurant> restaurants = mapper.readValue(resBody.toString(), new TypeReference<List<Restaurant>>(){});

        //check input isn't null
        if (validator.inputStringValidator(orderStr)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {
            try { //read json into order class
                order = mapper.readValue(orderStr, Order.class);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //if order validation is already done, assign the existing values
            if (order.getOrderValidationCode() != OrderValidationCode.UNDEFINED && order.getOrderStatus() != OrderStatus.UNDEFINED){
                result.setStatus(order.getOrderStatus());
                result.setValidationCode(order.getOrderValidationCode());
            }
            else {

                List<OrderValidationCode> codes = new ArrayList<>();

                LocalDate orderDate = LocalDate.parse(order.getOrderDate());
                YearMonth orderDateYM = YearMonth.from(orderDate);

                //check for all errors and add responses to list
                codes.add(order.getCreditCardInformation().validateCreditCard(orderDateYM));
                codes.add(order.validatePizzas(validator, restaurants, orderDate));

                //as only 1 error can occur, check all codes and take the 1 that is NOT undefined
                for (OrderValidationCode code : codes) {
                    if (code != OrderValidationCode.UNDEFINED) {
                        result.setValidationCode(code);
                        result.setStatus(OrderStatus.INVALID);
                    }
                }

                //if no errors were found, set it to valid and no error
                if (result.getOrderValidationCode() == OrderValidationCode.UNDEFINED) {
                    result.setValidationCode(OrderValidationCode.NO_ERROR);
                    result.setStatus(OrderStatus.VALID);
                }
            }
        }
        return ResponseEntity.ok(result);
    }



    @PostMapping("/calcDeliveryPath")
    public ResponseEntity<List<LongLat>> calcDeliveryPath (@RequestBody String orderStr) throws IOException {

        Order order;

        //get data from REST service
        StringBuilder resBody = getDataFromREST("https://ilp-rest-2024.azurewebsites.net/restaurants");
        List<Restaurant> restaurants = mapper.readValue(resBody.toString(), new TypeReference<List<Restaurant>>() {});

        StringBuilder centralBody = getDataFromREST("https://ilp-rest-2024.azurewebsites.net/centralArea");
        Region centralArea = mapper.readValue(centralBody.toString(), Region.class);

        StringBuilder noFlyBody = getDataFromREST("https://ilp-rest-2024.azurewebsites.net/noFlyZones");
        List<Region> noFlyZones = mapper.readValue(noFlyBody.toString(), new TypeReference<List<Region>>() {});

        LongLat appleton = new LongLat();
        appleton.setLng(APPLETON_LNG);
        appleton.setLat(APPLETON_LAT);

        //ensure input is not null and valid object format
        if (validator.inputStringValidator(orderStr)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {
            try {
                order = mapper.readValue(orderStr, Order.class);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        //check order is valid
        OrderValidationResult orderValidation = validateOrder(orderStr).getBody();
        if (orderValidation.getOrderStatus() != OrderStatus.VALID) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //get restaurant and its position
        Restaurant orderRestaurant = getOrderRestaurant(order, restaurants);
        LongLat resPos = orderRestaurant.getLocation();

        List<LongLat> path = a_Star(appleton, resPos, centralArea, noFlyZones);

        //no path found
        if (path == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(path);
    }



    @PostMapping("/calcDeliveryPathAsGeoJson")
    public ResponseEntity<GeoJSON> calcDeliveryPathAsGeoJson (@RequestBody String orderStr) throws IOException {

        //check for valid path
        List<LongLat> path = calcDeliveryPath(orderStr).getBody();
        if (path == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //initialise objects for geojson
        List<Feature> features = new ArrayList<>();
        Property properties = new Property();

        List<List<Double>> pathGeometryPoints = new ArrayList<>();

        for (LongLat point : path){
            pathGeometryPoints.add(Arrays.asList(point.getLng(), point.getLat()));
        }

        Geometry pathGeometry = new Geometry("LineString", pathGeometryPoints);
        Feature pathFeature = new Feature(pathGeometry, properties);

        features.add(pathFeature);

        GeoJSON geoJSON = new GeoJSON(features);
        return ResponseEntity.ok(geoJSON);
    }



//helper/non-main functions--------------------------------------


    public boolean getOnBoundary(LongLat position, Path2D regionPoly){//checking if point is in polygon

        Double lng = position.getLng();
        Double lat = position.getLat();

        Rectangle2D rect = new Rectangle2D.Double(lng - TOLERANCE, lat - TOLERANCE, 2 * TOLERANCE, 2 * TOLERANCE);
        boolean onBoundary = regionPoly.intersects(rect); //checking if point is on boundary

        return onBoundary;
    }


    public Restaurant getOrderRestaurant (Order order, List < Restaurant > restaurants){
        //get restaurant of given order
        String pizza = order.getPizzasInOrder().get(0).getName();
        List<String> menuPizzaNames;

        for (Restaurant restaurant : restaurants) {
            //get all pizza names from menu
            menuPizzaNames = restaurant.getMenu().stream().map(Pizza::getName).toList();

            if (menuPizzaNames.contains(pizza)) {
                return restaurant;
            }
        }
        return null;
    }


    public List<LongLat> a_Star (LongLat appleton, LongLat resPos, Region centralArea, List < Region > noFlyZones) throws JsonProcessingException {
        Set<Node> closed = new HashSet<>() {
        };

        //priority queue for selecting next node to expand (node with lowest f chosen)
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));

        double g;
        double h;
        boolean closeGap;

        LongLatPair distanceChecker = new LongLatPair();
        distanceChecker.setPosition1(resPos);
        distanceChecker.setPosition2(appleton);

        Node current;
        Node gapCloser;

        //initialising the start node at the restaurant
        Node start = new Node(resPos, null);
        start.setG(0);
        start.setH(parseDouble(getDistanceTo(mapper.writeValueAsString(distanceChecker)).getBody()));
        start.setF();

        open.add(start);

        List<Node> successors;

        List<LongLat> path = null;

        //carry out a* search algorithm
        while (!open.isEmpty()) {

            current = open.poll();
            successors = generateSuccessors(current, centralArea, noFlyZones);

            //check to see if gap should be closed instead of doing heuristic search
            closeGap = current.getH() > (30 * MOVEMENT);

            for (Node successor : successors) {

                distanceChecker.setPosition1(successor.getPos());

                //return path from successor to goal if close to appleton
                if (isClose(mapper.writeValueAsString(distanceChecker)).getBody()) {
                    path = getPath(successor);
                    return path;
                } else {
                    //calculate f for successor
                    g = current.getG() + MOVEMENT;
                    h = parseDouble(getDistanceTo(mapper.writeValueAsString(distanceChecker)).getBody());

                    successor.setG(g);
                    successor.setH(h);
                    successor.setF();

                    if (skipNode(successor, open) || skipNode(successor, closed) || current.getH() < h) {
                        continue;
                    }
                    if (!closeGap) {
                        open.add(successor);
                    }
                }
            }

            if (closeGap) {
                //if closing gap (30 moves away), add the node closest to the goal (lowest h) to open list
                gapCloser = Collections.min(successors, Comparator.comparingDouble(Node::getH));
                open.add(gapCloser);
            }
            closed.add(current);
        }
        return path;
    }


    public List<Node> generateSuccessors (Node node, Region central, List < Region > noFly) throws JsonProcessingException {

        List<Node> successors = new ArrayList<>();

        isInRegionRequest centralCompareOld = new isInRegionRequest();
        centralCompareOld.setRegion(central);

        isInRegionRequest centralCompareNew = new isInRegionRequest();
        centralCompareNew.setRegion(central);

        isInRegionRequest noFlyCompare = new isInRegionRequest();
        boolean noFlyBool;
        LongLat parentPos = new LongLat();

        //storing current's parent to ensure it isn't remade
        if (node.getParent() != null) {
            parentPos.setLng(node.getParent().getPos().getLng());
            parentPos.setLat(node.getParent().getPos().getLat());
        }

        //make successor for each movement angle
        for (Double angle : ANGLES) {

            noFlyBool = false;

            if (angle == 999.0) {
                break;
            }

            //find the position of the node by moving from current node at the current angle
            MoveRequest posAngle = new MoveRequest();
            posAngle.setStart(node.getPos());
            posAngle.setAngle(angle);

            //get position of successors
            String posString = nextPosition(mapper.writeValueAsString(posAngle)).getBody();
            LongLat pos = mapper.readValue(posString, LongLat.class);

            centralCompareOld.setPosition(node.getPos());
            centralCompareNew.setPosition(pos);

            //don't create successor if current is in central and successor is not (never leaves central)
            if (isInRegion(mapper.writeValueAsString(centralCompareOld)).getBody() &&
                    !isInRegion(mapper.writeValueAsString(centralCompareNew)).getBody()) {

                continue;
            }

            noFlyCompare.setPosition(pos);
            for (Region nfz : noFly) {
                noFlyCompare.setRegion(nfz);

                if (isInRegion(mapper.writeValueAsString(noFlyCompare)).getBody()) {
                    noFlyBool = true;
                    break;
                }
            }

            //do not create successor if it is in NFZ
            if (noFlyBool) {
                continue;
            }

            //do not create successor if it is in current's parent's position
            if (node.getParent() != null && parentPos.getLng().equals(pos.getLng())
                    && parentPos.getLat().equals(pos.getLat())) {
                continue;
            }

            Node successor = new Node(pos, node);

            successors.add(successor);
        }
        return successors;
    }


    //skips successor if node in the same position has a lower f score
    public boolean skipNode (Node successor, Collection < Node > list){
        for (Node node : list) {
            LongLat pos = node.getPos();

            if (pos.getLat().equals(successor.getPos().getLat()) && pos.getLng().equals(successor.getPos().getLng()) &&
                    node.getF() <= successor.getF()) {
                return true;
            }
        }
        return false;
    }


    public List<LongLat> getPath (Node node){

        List<LongLat> path = new ArrayList<>();

        //follows path of parents until start reached
        while (node != null) {

            path.add(node.getPos());
            node = node.getParent();
        }

        //adding hovers
        path.add(path.get(path.size() - 1));
        Collections.reverse(path);
        path.add(path.get(path.size() - 1));

        return path;
    }


    public StringBuilder getDataFromREST (String urlStr) throws IOException {

        HttpURLConnection conn;

        try { //get info from Rest
            URL queryURL = new URL(urlStr);
            conn = (HttpURLConnection) queryURL.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != 200) { //bad request if it can't connect
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        //read response body as string
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        reader.close();
        return body;
    }
}