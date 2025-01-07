package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.dataTypes.OrderStatus;
import uk.ac.ed.inf.dataTypes.OrderValidationCode;
import uk.ac.ed.inf.dataTypes.OrderValidationResult;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderValidationTests {

    RESTController restController = new RESTController();


    //credit card related validation
    @Test
    public void validateOrder_validOrderTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/25\",\"cvv\":\"382\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.VALID,
                OrderValidationCode.NO_ERROR);

        assertEquals(expected, result);
    }


    @Test
    public void validateOrder_invalidCardNumTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111\"," +
                "\"creditCardExpiry\":\"05/25\",\"cvv\":\"382\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.CARD_NUMBER_INVALID);

        assertEquals(expected, result);
    }


    @Test
    public void validateOrder_invalidExpiryTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/09\",\"cvv\":\"382\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.EXPIRY_DATE_INVALID);

        assertEquals(expected, result);
    }


    @Test
    public void validateOrder_invalidCVVTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/26\",\"cvv\":\"3833\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.CVV_INVALID);

        assertEquals(expected, result);
    }


    //pizza and restaurant related validation
    @Test
    public void validateOrder_incorrectTotalTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":999," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/26\",\"cvv\":\"383\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.TOTAL_INCORRECT);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_invalidPizzaPriceTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":1600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/26\",\"cvv\":\"383\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.PRICE_FOR_PIZZA_INVALID);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_notDefinedPizzaTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":1600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"undefined pizza\",\"priceInPence\":999}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/26\",\"cvv\":\"383\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.PIZZA_NOT_DEFINED);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_maxPizzaTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":5900," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}, " +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}, " +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}, " +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/26\",\"cvv\":\"383\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_multipleRestaurantsTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-07\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2900," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R7: Hot, hotter, the hottest\",\"priceInPence\":1400}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/26\",\"cvv\":\"383\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_closedRestaurantTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-11\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[{\"name\":\"R2: Meat Lover\",\"priceInPence\":1400}," +
                "{\"name\":\"R2: Vegan Delight\",\"priceInPence\":1100}]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/26\",\"cvv\":\"383\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.RESTAURANT_CLOSED);

        assertEquals(expected, result);
    }

    @Test
    public void validateOrder_emptyOrderTest() throws IOException {

        String order = "{\"orderNo\":\"6E703605\",\"orderDate\":\"2025-01-08\",\"orderStatus\":\"UNDEFINED\"," +
                "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":2600," +
                "\"pizzasInOrder\":[]," +
                "\"creditCardInformation\":{\"creditCardNumber\":\"1111111111111111\"," +
                "\"creditCardExpiry\":\"05/26\",\"cvv\":\"383\"}}";

        OrderValidationResult result = restController.validateOrder(order).getBody();
        OrderValidationResult expected = new OrderValidationResult(OrderStatus.INVALID,
                OrderValidationCode.EMPTY_ORDER);

        assertEquals(expected, result);
    }
}
