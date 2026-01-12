package uk.ac.ed.inf;

import uk.ac.ed.inf.dataTypes.OrderStatus;
import uk.ac.ed.inf.dataTypes.OrderValidationCode;
import uk.ac.ed.inf.dataTypes.OrderValidationResult;

import static uk.ac.ed.inf.OrderTestBuilder.CardNum.*;
import static uk.ac.ed.inf.OrderTestBuilder.Expiry.EXPIRED;
import static uk.ac.ed.inf.OrderTestBuilder.Expiry.INVALID;
import static uk.ac.ed.inf.OrderTestBuilder.OrderTime.CLOSED;
import static uk.ac.ed.inf.OrderTestBuilder.PizzaCount.FIVE;
import static uk.ac.ed.inf.OrderTestBuilder.PizzaCount.ZERO;
import static uk.ac.ed.inf.OrderTestBuilder.Restaurant.MULTIPLE;
import static uk.ac.ed.inf.dataTypes.OrderValidationCode.*;

public class ResultOracle {

    static OrderValidationResult evaluate(OrderTestBuilder b) {

        if (b.getExpiry() == EXPIRED || b.getExpiry() == INVALID) return new OrderValidationResult(OrderStatus.INVALID, EXPIRY_DATE_INVALID);
        if (b.getCardNum() == SHORT || b.getCardNum() == LONG || b.getCardNum() == NON_NUMERIC) return new OrderValidationResult(OrderStatus.INVALID, OrderValidationCode.CARD_NUMBER_INVALID);
        if (b.getCVV() == OrderTestBuilder.CVV.SHORT || b.getCVV() == OrderTestBuilder.CVV.LONG || b.getCVV() == OrderTestBuilder.CVV.NON_NUMERIC) return new OrderValidationResult(OrderStatus.INVALID, CVV_INVALID);
        if (b.getPizzaCount() == ZERO) return new OrderValidationResult(OrderStatus.INVALID, EMPTY_ORDER);
        if (b.getPizzaCount() == FIVE) return new OrderValidationResult(OrderStatus.INVALID, MAX_PIZZA_COUNT_EXCEEDED);
        if (b.getPizzaDef() == OrderTestBuilder.PizzaDef.UNDEFINED) return new OrderValidationResult(OrderStatus.INVALID, PIZZA_NOT_DEFINED);
        if (b.getOrderTime() == CLOSED) return new OrderValidationResult(OrderStatus.INVALID, RESTAURANT_CLOSED);
        if (b.getPizzaPrice() == OrderTestBuilder.PizzaPrice.LOW || b.getPizzaPrice() == OrderTestBuilder.PizzaPrice.HIGH) return new OrderValidationResult(OrderStatus.INVALID, PRICE_FOR_PIZZA_INVALID);
        if (b.getRestaurant() == MULTIPLE) return new OrderValidationResult(OrderStatus.INVALID, PIZZA_FROM_MULTIPLE_RESTAURANTS);
        if (b.getTotal() == OrderTestBuilder.Total.LOW || b.getTotal() == OrderTestBuilder.Total.HIGH) return new OrderValidationResult(OrderStatus.INVALID, TOTAL_INCORRECT);

        return new OrderValidationResult(OrderStatus.VALID, NO_ERROR);
    }
}
