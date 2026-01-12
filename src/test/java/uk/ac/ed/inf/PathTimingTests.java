package uk.ac.ed.inf;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.ed.inf.SystemConstants.MOVEMENT;

public class PathTimingTests {

    RESTController rc = new RESTController();

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


    //various NFZ tests
    @Test
    public void routeTiming_corridorNFZsTest() throws Exception {

        for (int corridors : new int[]{1, 5, 10, 20, 40, 80, 160, 320, 640, 1280, 2560, 5120, 10240, 20480, 40960}) {

            String nfz = NfzGenerator.generateCorridors(corridors);

            stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
            stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
            stubFor(get("/noFlyZones").willReturn(okJson(nfz)));

            String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                    OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                    OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                    OrderTestBuilder.OrderTime.OPEN).buildJson();

            long start = System.nanoTime();
            rc.calcDeliveryPath(order);
            double seconds = (System.nanoTime() - start) / 1e9;

            System.out.printf("%d corridor(s) = %.3fs%n", corridors, seconds);

            assertTrue(seconds < 60, "Timeout at " + corridors);
        }
    }

    @Test
    public void routeTiming_radialNFZsTest() throws Exception {

        for (int nfzs : new int[]{1, 5, 10, 20, 40, 80, 160, 320, 640, 1280, 2560}) {

            String nfz = NfzGenerator.generateRadial(nfzs, MOVEMENT * 3);

            stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
            stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
            stubFor(get("/noFlyZones").willReturn(okJson(nfz)));

            String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                    OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                    OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                    OrderTestBuilder.OrderTime.OPEN).buildJson();

            long start = System.nanoTime();
            rc.calcDeliveryPath(order);
            double seconds = (System.nanoTime() - start) / 1e9;

            System.out.printf("%d NFZ(s) = %.3fs%n", nfzs, seconds);

            assertTrue(seconds < 60, "Timeout at " + nfzs);
        }
    }

    @Test
    public void routeTiming_mazeTest() throws Exception {

        for (int nfzs : new int[]{1, 2, 4, 8, 16, 32, 64, 128}) {

            String nfz = NfzGenerator.generateMaze(nfzs, nfzs, MOVEMENT * 2);

            stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
            stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
            stubFor(get("/noFlyZones").willReturn(okJson(nfz)));

            String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                    OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                    OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                    OrderTestBuilder.OrderTime.OPEN).buildJson();

            long start = System.nanoTime();
            rc.calcDeliveryPath(order);
            double seconds = (System.nanoTime() - start) / 1e9;

            System.out.printf("%d NFZ(s) = %.3fs%n", nfzs, seconds);

            assertTrue(seconds < 60, "Timeout at " + nfzs);
        }
    }

    @Test
    public void routeTiming_realisticScatterTest() throws Exception {

        for (int i = 0; i < 20; i++) {

            String nfz = NfzGenerator.generateScatter(50, i);

            stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
            stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
            stubFor(get("/noFlyZones").willReturn(okJson(nfz)));

            String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                    OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                    OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                    OrderTestBuilder.OrderTime.OPEN).buildJson();

            long start = System.nanoTime();
            rc.calcDeliveryPath(order);
            double seconds = (System.nanoTime() - start) / 1e9;

            System.out.printf("%d NFZS, loop %d = %.3fs%n", 50, i, seconds);

            assertTrue(seconds < 60);
        }
    }

    @Test
    public void routeTiming_scatterTest() throws Exception {

        for (int nfzs : new int[]{1, 5, 10, 20, 40, 80, 160, 320, 640, 1280, 2560, 5120, 10240, 20480, 40960}) {

            String nfz = NfzGenerator.generateScatter(nfzs, nfzs);

            stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
            stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
            stubFor(get("/noFlyZones").willReturn(okJson(nfz)));

            String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                    OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                    OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                    OrderTestBuilder.OrderTime.OPEN).buildJson();

            long start = System.nanoTime();
            rc.calcDeliveryPath(order);
            double seconds = (System.nanoTime() - start) / 1e9;

            System.out.printf("%d NFZ(s) = %.3fs%n", nfzs, seconds);

            assertTrue(seconds < 60, "Timeout at " + nfzs);
        }
    }

    @Test
    public void routeTiming_wallTest() throws Exception {

        for (int nfzs : new int[]{5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}) {

            String nfz = NfzGenerator.generateWall(nfzs);

            stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_WEST)));
            stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
            stubFor(get("/noFlyZones").willReturn(okJson(nfz)));

            String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                    OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                    OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                    OrderTestBuilder.OrderTime.OPEN).buildJson();

            long start = System.nanoTime();
            rc.calcDeliveryPathAsGeoJson(order);
            double seconds = (System.nanoTime() - start) / 1e9;

            System.out.printf("%d NFZ(s) = %.3fs%n", nfzs, seconds);

            assertTrue(seconds < 60, "Timeout at " + nfzs);
        }
    }

    @Test
    public void routeTiming_distanceTest_outsideCentral() throws Exception {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_OUTSIDE_CENTRAL)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.EMPTY_LIST)));

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        long start = System.nanoTime();
        rc.calcDeliveryPathAsGeoJson(order);
        double seconds = (System.nanoTime() - start) / 1e9;

        System.out.printf("Outside central restaurant = %.3fs%n", seconds);

        assertTrue(seconds < 60, "Timeout");
    }

    @Test
    public void routeTiming_distanceTest_far() throws Exception {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_FAR)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.EMPTY_LIST)));

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        long start = System.nanoTime();
        rc.calcDeliveryPathAsGeoJson(order);
        double seconds = (System.nanoTime() - start) / 1e9;

        System.out.printf("Far restaurant = %.3fs%n", seconds);

        assertTrue(seconds < 60, "Timeout");
    }

    @Test
    public void routeTiming_distanceTest_extreme() throws Exception {

        stubFor(get("/restaurants").willReturn(okJson(TestMaps.RESTAURANTS_EXTREME)));
        stubFor(get("/centralArea").willReturn(okJson(TestMaps.CENTRAL)));
        stubFor(get("/noFlyZones").willReturn(okJson(TestMaps.EMPTY_LIST)));

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        long start = System.nanoTime();
        rc.calcDeliveryPathAsGeoJson(order);
        double seconds = (System.nanoTime() - start) / 1e9;

        System.out.printf("Extreme restaurant = %.3fs%n", seconds);

        assertTrue(seconds < 60, "Timeout");
    }
}

