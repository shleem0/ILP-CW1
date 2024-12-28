package uk.ac.ed.inf.dataTypes;

import uk.ac.ed.inf.InputValidator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class Order {

    private String orderNo;
    private String orderDate;
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;

    private int priceTotalInPence;
    private List<Pizza> pizzasInOrder;

    private CreditCard creditCardInformation;

    public String getOrderNo(){
        return orderNo;
    }
    public void setOrderNo(String orderNo){
        this.orderNo = orderNo;
    }


    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate){
        this.orderDate = orderDate;
    }


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }


    public OrderValidationCode getOrderValidationCode() {
        return orderValidationCode;
    }
    public void setOrderValidationCode(OrderValidationCode orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }

    public int getPriceTotalInPence() {
        return priceTotalInPence;
    }
    public void setPriceTotalInPence(int priceTotalInPence) {
        this.priceTotalInPence = priceTotalInPence;
    }


    public List<Pizza> getPizzasInOrder() {
        return pizzasInOrder;
    }
    public void setPizzasInOrder(List<Pizza> pizzasInOrder) {
        this.pizzasInOrder = pizzasInOrder;
    }


    public CreditCard getCreditCardInformation() {
        return creditCardInformation;
    }
    public void setCreditCardInformation(CreditCard creditCardInformation) {
        this.creditCardInformation = creditCardInformation;
    }


    public OrderValidationCode validatePizzas(InputValidator validator, List<Restaurant> restaurants, LocalDate orderDate) {
        int sum = 0;
        boolean pizzaFound;

        List<Pizza> menu;
        List<String> menuPizzaNames;
        List<String> openingDays;
        String resName;
        String prevResName = null;

        if (pizzasInOrder.isEmpty()) {
            System.out.println("Empty");
            return OrderValidationCode.EMPTY_ORDER;

        } else if (pizzasInOrder.size() > 4) {
            System.out.println("Max pizzas");
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;

        } else {

            for (Pizza pizza : pizzasInOrder) {
                pizzaFound = false;
                sum += pizza.getPrice();

                if (validator.inputStringValidator(pizza.getName())){
                    return OrderValidationCode.PIZZA_NOT_DEFINED;
                }

                for (Restaurant restaurant : restaurants) {

                    menu = restaurant.getMenu();
                    resName = restaurant.getName();
                    menuPizzaNames = menu.stream().map(Pizza::getName).toList();

                    if (menuPizzaNames.contains(pizza.getName())) {
                        pizzaFound = true;

                        DayOfWeek day = orderDate.getDayOfWeek();
                        openingDays = restaurant.getDays();

                        if (!openingDays.contains(day.toString().toUpperCase())) {
                            System.out.println("Res closed");
                            return OrderValidationCode.RESTAURANT_CLOSED;
                        }

                        if (pizza.getPrice() != menu.get(menuPizzaNames.indexOf(pizza.getName())).getPrice()) {
                            System.out.println("Invalid price");
                            return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
                        }

                        if (!resName.equals(prevResName) && prevResName != null){
                            System.out.println("Multiple res");
                            return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
                        }
                        else{
                            prevResName = resName;
                        }

                    }
                }
                if (!pizzaFound) {
                    System.out.println("Undefined pizza");
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

