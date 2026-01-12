package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uk.ac.ed.inf.dataTypes.LongLat;
import uk.ac.ed.inf.dataTypes.Region;
import uk.ac.ed.inf.dataTypes.Restaurant;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

public class PathFindingTests {

    public PathFindingTests() throws JsonProcessingException {}

    RESTController rc = new RESTController();
    ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter());

    String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
            OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
            OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
            OrderTestBuilder.OrderTime.OPEN).buildJson();

    Region central = mapper.readValue(TestMaps.CENTRAL, new TypeReference<Region>() {});
    List<Region> nfzs = mapper.readValue(TestMaps.NO_FLY_SIMPLE, new TypeReference<List<Region>>() {});
    List<Restaurant> restaurants = mapper.readValue(TestMaps.RESTAURANTS_WEST, new TypeReference<List<Restaurant>>() {});

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @BeforeEach
    public void setup() {
        configureFor("localhost", wiremock.getPort());
        int port = wiremock.getPort();

        rc.setRestaurantURL("http://localhost:" + port + "/restaurants");
        rc.setCentralURL("http://localhost:" + port + "/centralArea");
        rc.setNfzURL("http://localhost:" + port + "/noFlyZones");

    }

    @Test
    public void calcDeliveryPath_straightLineTest() throws IOException {
        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.EMPTY_LIST)));

        List<LongLat> path = rc.calcDeliveryPath(order).getBody();
        assertNotNull(path);

        nfzs = mapper.readValue(TestMaps.EMPTY_LIST, new TypeReference<List<Region>>() {});
        List<String> errors = RouteOracle.validate(path, central, nfzs, restaurants.get(0).getLocation());
        assertTrue(errors.isEmpty());
    }

    @Test
    public void calcDeliveryPath_simpleNFZTest() throws IOException {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.NO_FLY_SIMPLE)));

        List<LongLat> path = rc.calcDeliveryPath(order).getBody();
        assertNotNull(path);

        List<String> errors = RouteOracle.validate(path, central, nfzs, restaurants.get(0).getLocation());
        assertTrue(errors.isEmpty());
    }

    @Test
    public void calcDeliveryPath_goalInNFZTest() throws IOException {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.NO_FLY_IMPOSSIBLE_GOAL)));

        ResponseEntity<List<LongLat>> pathResponse = rc.calcDeliveryPath(order);
        assertEquals(pathResponse.getStatusCode(), HttpStatusCode.valueOf(400));

        List<LongLat> path = pathResponse.getBody();
        assertNull(path);
    }

    @Test
    public void calcDeliveryPath_startInNFZTest() throws IOException {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.NO_FLY_IMPOSSIBLE_START)));

        ResponseEntity<List<LongLat>> pathResponse = rc.calcDeliveryPath(order);
        assertEquals(pathResponse.getStatusCode(), HttpStatusCode.valueOf(400));

        List<LongLat> path = rc.calcDeliveryPath(order).getBody();
        assertNull(path);
    }

    @Test
    public void calcDeliveryPath_NFZWallTest() throws IOException {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.NO_FLY_WALL)));

        List<LongLat> path = rc.calcDeliveryPath(order).getBody();
        assertNotNull(path);

        nfzs = mapper.readValue(TestMaps.NO_FLY_WALL, new TypeReference<List<Region>>() {});
        List<String> errors = RouteOracle.validate(path, central, nfzs, restaurants.get(0).getLocation());
        assertTrue(errors.isEmpty());
    }

    @Test
    public void calcDeliveryPath_NFZCorridorTest() throws IOException {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.NO_FLY_CORRIDOR)));

        List<LongLat> path = rc.calcDeliveryPath(order).getBody();
        assertNotNull(path);

        nfzs = mapper.readValue(TestMaps.NO_FLY_CORRIDOR, new TypeReference<List<Region>>() {});
        List<String> errors = RouteOracle.validate(path, central, nfzs, restaurants.get(0).getLocation());
        assertTrue(errors.isEmpty());
    }

    @Test
    public void calcDeliveryPath_NFZCorridorSouthStartTest() throws IOException {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_SOUTHER)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.NO_FLY_CORRIDOR)));

        List<LongLat> path = rc.calcDeliveryPath(order).getBody();
        assertNotNull(path);

        restaurants = mapper.readValue(TestMaps.RESTAURANTS_SOUTHER, new TypeReference<List<Restaurant>>(){});
        nfzs = mapper.readValue(TestMaps.NO_FLY_CORRIDOR, new TypeReference<List<Region>>() {});
        List<String> errors = RouteOracle.validate(path, central, nfzs, restaurants.get(0).getLocation());
        assertTrue(errors.isEmpty());
    }

    @Test
    public void calcDeliveryPath_startOutsideCentralTest() throws IOException {
        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_OUTSIDE_CENTRAL)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.EMPTY_LIST)));

        List<LongLat> path = rc.calcDeliveryPath(order).getBody();
        assertNotNull(path);

        nfzs = mapper.readValue(TestMaps.EMPTY_LIST, new TypeReference<List<Region>>() {});
        restaurants = mapper.readValue(TestMaps.RESTAURANTS_OUTSIDE_CENTRAL, new TypeReference<List<Restaurant>>() {});
        List<String> errors = RouteOracle.validate(path, central, nfzs, restaurants.get(0).getLocation());
        assertTrue(errors.isEmpty());
    }

    @Test
    public void calcDeliveryPath_invalidOrderTest() throws IOException {
        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.EMPTY_LIST)));

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.NON_NUMERIC,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        ResponseEntity<List<LongLat>> pathResponse = rc.calcDeliveryPath(order);
        assertEquals(pathResponse.getStatusCode(), HttpStatusCode.valueOf(400));

        List<LongLat> path = rc.calcDeliveryPath(order).getBody();
        assertNull(path);
    }
}
