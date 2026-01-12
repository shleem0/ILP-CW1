package uk.ac.ed.inf;

import uk.ac.ed.inf.dataTypes.CreditCard;
import uk.ac.ed.inf.dataTypes.Pizza;

import java.util.List;
import java.util.stream.Collectors;

public class JsonBuilder {

    public static String build(String date, List<Pizza> pizzas, CreditCard card, int total) {

        String pizzaJson = pizzas.stream()
                .map(p -> String.format("{\"name\":\"%s\",\"priceInPence\":%d}", p.name, p.priceInPence))
                .collect(Collectors.joining(","));

        return String.format(
                "{\"orderNo\":\"AUTO\",\"orderDate\":\"%s\",\"orderStatus\":\"UNDEFINED\"," +
                        "\"orderValidationCode\":\"UNDEFINED\",\"priceTotalInPence\":%d," +
                        "\"pizzasInOrder\":[%s]," +
                        "\"creditCardInformation\":{\"creditCardNumber\":\"%s\"," +
                        "\"creditCardExpiry\":\"%s\",\"cvv\":\"%s\"}}",
                date, total, pizzaJson, card.getCreditCardNumber(), card.getCreditCardExpiry(), card.getCVV()
        );
    }
}
