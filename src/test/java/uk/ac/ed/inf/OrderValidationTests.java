package uk.ac.ed.inf;

import uk.ac.ed.inf.dataTypes.OrderStatus;
import uk.ac.ed.inf.dataTypes.OrderValidationCode;
import uk.ac.ed.inf.dataTypes.OrderValidationResult;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import static org.junit.jupiter.api.Assertions.*;

public class OrderValidationTests {

    RESTController restController = new RESTController();

    @Test
    public void validateOrder_validOrderTest() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.VALID,
                OrderValidationCode.NO_ERROR);

        assertEquals(expected, result);
    }

    //empty order string
    @Test
    public void validateOrder_emptyJsonTest() throws IOException {

        String order = "";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        assertNull(result);
    }

    @Test
    public void validateOrder_malformedJsonTest() throws IOException {

        String order = "<NOT ORDER>";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        assertNull(result);
    }

    //SINGLE RULE TESTS

    //credit card number validation
    @Test
    public void validateOrder_invalidCardNumTest_short() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.SHORT, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.CARD_NUMBER_INVALID);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_invalidCardNumTest_long() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.LONG, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.CARD_NUMBER_INVALID);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_invalidCardNumTest_nonnumeric() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.NON_NUMERIC, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.CARD_NUMBER_INVALID);

        assertEquals(expected, result);
    }


    //credit card expiry date validation
    @Test
    public void validateOrder_invalidExpiryTest() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.EXPIRED, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.EXPIRY_DATE_INVALID);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_invalidExpiryTest_nonDate() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.INVALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.EXPIRY_DATE_INVALID);

        assertEquals(expected, result);
    }


    @Test
    public void validateOrder_invalidCVVTest_short() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.SHORT,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.CVV_INVALID);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_invalidCVVTest_long() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.LONG,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.CVV_INVALID);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_invalidCVVTest_nonnumeric() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.NON_NUMERIC,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.CVV_INVALID);

        assertEquals(expected, result);
    }


    //pizza and restaurant related validation
    @Test
    public void validateOrder_incorrectTotalTest_high() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.HIGH,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.TOTAL_INCORRECT);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_incorrectTotalTest_low() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.LOW,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.TOTAL_INCORRECT);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_notDefinedPizzaTest() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.UNDEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.PIZZA_NOT_DEFINED);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_invalidPizzaPriceTest_low() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.LOW, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();

        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.PRICE_FOR_PIZZA_INVALID);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_invalidPizzaPriceTest_high() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.HIGH, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();

        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.PRICE_FOR_PIZZA_INVALID);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_pizzaCountTest_high() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.FIVE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_pizzaCountTest_low() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ZERO, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.EMPTY_ORDER);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_multipleRestaurantsTest() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.MULTIPLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.OPEN).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_closedRestaurantTest() throws IOException {

        String order = new OrderTestBuilder(OrderTestBuilder.CardNum.VALID, OrderTestBuilder.CVV.VALID,
                OrderTestBuilder.Expiry.VALID, OrderTestBuilder.PizzaCount.ONE, OrderTestBuilder.Restaurant.SINGLE,
                OrderTestBuilder.PizzaDef.DEFINED, OrderTestBuilder.PizzaPrice.CORRECT, OrderTestBuilder.Total.CORRECT,
                OrderTestBuilder.OrderTime.CLOSED).buildJson();

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.RESTAURANT_CLOSED);

        assertEquals(expected, result);
    }


    //PAIRWISE COMBINATORIAL TESTS
    @ParameterizedTest(name = "{index} → {0}")
    @MethodSource("uk.ac.ed.inf.PairwiseReducer#pairwiseOrders")
    public void validateOrder_pairwiseCombos(PairwiseCase c) throws IOException {

        String json = c.builder().buildJson();

        OrderValidationResult actual = restController.validateOrder(json).getBody();

        assertEquals(c.expected(), actual);
    }
}


