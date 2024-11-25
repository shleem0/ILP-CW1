package uk.ac.ed.inf.dataTypes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class Order {

    public String orderNo;
    public String orderDate;
    public OrderStatus orderStatus;
    public OrderValidationCode orderValidationCode;

    public int priceTotalInPence;
    public List<Pizza> pizzasInOrder;

    public CreditCard creditCardInformation;


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public OrderValidationCode getOrderValidationCode() {
        return orderValidationCode;
    }

    public CreditCard getCreditCardInformation() {
        return creditCardInformation;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public OrderValidationCode validatePizzas(List<Restaurant> restaurants, LocalDate orderDate) {
        int sum = 0;
        boolean pizzaFound;
        List<Pizza> menu;
        List<String> menuPizzaNames;
        List<String> openingDays;
        String prevResCode = "";

        if (pizzasInOrder.isEmpty()) {
            return OrderValidationCode.EMPTY_ORDER;
        } else if (pizzasInOrder.size() > 4) {
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        } else {

            for (Pizza pizza : pizzasInOrder) {
                pizzaFound = false;
                sum += pizza.getPrice();

                String pizzaResCode = pizza.getName().split(" ")[0].substring(0, 2);

                if(pizzaResCode.charAt(0) != 'R') {
                    return OrderValidationCode.PIZZA_NOT_DEFINED;
                }
                else if (!pizzaResCode.equals(prevResCode) && !prevResCode.isEmpty()) {
                    return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
                }

                prevResCode = pizzaResCode;

                for (Restaurant restaurant : restaurants) {

                    menu = restaurant.getMenu();
                    menuPizzaNames = menu.stream().map(Pizza::getName).toList();

                    if (menuPizzaNames.contains(pizza.getName())) {
                        pizzaFound = true;
                        DayOfWeek day = orderDate.getDayOfWeek();
                        openingDays = restaurant.getDays();

                        if (!openingDays.contains(day.toString().toUpperCase())) {
                            return OrderValidationCode.RESTAURANT_CLOSED;
                        }
                        if (pizza.getPrice() != menu.get(menuPizzaNames.indexOf(pizza.getName())).getPrice()) {
                            return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
                        }
                    }
                }
                if (!pizzaFound) {
                    return OrderValidationCode.PIZZA_NOT_DEFINED;
                }
            }
            if (sum + 100 != priceTotalInPence) {
                return OrderValidationCode.TOTAL_INCORRECT;
            }
            return OrderValidationCode.UNDEFINED;
        }
    }
}

