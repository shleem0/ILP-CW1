package uk.ac.ed.inf;

import uk.ac.ed.inf.dataTypes.CreditCard;
import uk.ac.ed.inf.dataTypes.Pizza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderTestBuilder {

    enum CardNum { VALID, SHORT, LONG, NON_NUMERIC }
    enum CVV { VALID, SHORT, LONG, NON_NUMERIC }
    enum Expiry { VALID, EXPIRED, INVALID }
    enum PizzaCount { ONE, ZERO, FIVE }
    enum Restaurant { SINGLE, MULTIPLE }
    enum PizzaDef { DEFINED, UNDEFINED }
    enum PizzaPrice { CORRECT, LOW, HIGH }
    enum Total { CORRECT, LOW, HIGH }
    enum OrderTime { OPEN, CLOSED }

    public OrderTestBuilder(CardNum cardNum, CVV cvv, Expiry expiry, PizzaCount pizzaCount, Restaurant restaurant,
                            PizzaDef pizzaDef, PizzaPrice pizzaPrice, Total total, OrderTime orderTime) {
        this.cardNum = cardNum;
        this.cvv = cvv;
        this.expiry = expiry;
        this.pizzaCount = pizzaCount;
        this.restaurant = restaurant;
        this.pizzaDef = pizzaDef;
        this.pizzaPrice = pizzaPrice;
        this.total = total;
        this.orderTime = orderTime;
    }

    private CardNum cardNum = CardNum.VALID;
    public void setCardNum(CardNum cardNum) {
        this.cardNum = cardNum;
    }
    public CardNum getCardNum() {
        return cardNum;
    }

    private CVV cvv = CVV.VALID;
    public void setCVV(CVV cvv) {
        this.cvv = cvv;
    }
    public CVV getCVV() {
        return cvv;
    }

    private Expiry expiry = Expiry.VALID;
    public void setExpiry(Expiry expiry) {
        this.expiry = expiry;
    }
    public Expiry getExpiry() {
        return expiry;
    }

    private PizzaCount pizzaCount = PizzaCount.ONE;
    public void setPizzaCount(PizzaCount pizzaCount) {
        this.pizzaCount = pizzaCount;
    }
    public PizzaCount getPizzaCount() {
        return pizzaCount;
    }

    private Restaurant restaurant = Restaurant.SINGLE;
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
    public Restaurant getRestaurant() {
        return restaurant;
    }

    private PizzaDef pizzaDef = PizzaDef.DEFINED;
    public void setPizzaDef(PizzaDef pizzaDef) {
        this.pizzaDef = pizzaDef;
    }
    public PizzaDef getPizzaDef() {
        return pizzaDef;
    }

    private PizzaPrice pizzaPrice = PizzaPrice.CORRECT;
    public void setPizzaPrice(PizzaPrice pizzaPrice) {
        this.pizzaPrice = pizzaPrice;
    }
    public PizzaPrice getPizzaPrice() {
        return pizzaPrice;
    }

    private Total total = Total.CORRECT;
    public void setTotal(Total total) {
        this.total = total;
    }
    public Total getTotal() {
        return total;
    }

    private OrderTime orderTime = OrderTime.OPEN;
    public void setOrderTime(OrderTime orderTime) {
        this.orderTime = orderTime;
    }
    public OrderTime getOrderTime() {
        return orderTime;
    }

    //Pizza generation
    private List<Pizza> makePizzas() {

        if (pizzaCount == PizzaCount.ZERO) return List.of();

        List<Pizza> pizzas = new ArrayList<>();

        if (pizzaDef == PizzaDef.UNDEFINED){
            pizzas.add(makePizza("<UNDEFINED PIZZA>"));
        }

        if (restaurant == Restaurant.SINGLE) {
            String name = DomainData.RESTAURANTS.get("R2")[0];
            pizzas.add(makePizza(name));
        } else {
            pizzas.add(makePizza("R1: Margarita"));
            pizzas.add(makePizza("R2: Meat Lover"));
        }

        if (pizzaCount == PizzaCount.FIVE) {
            while (pizzas.size() < 5) pizzas.add(makePizza("R1: Margarita"));
        }

        return pizzas;
    }

    private Pizza makePizza(String name) {
        int price = DomainData.PIZZAS.getOrDefault(name, 9999);
        if (pizzaPrice == PizzaPrice.LOW) {
            price -= 10;
        }
        else if (pizzaPrice == PizzaPrice.HIGH) {
            price += 10;
        }

        return new Pizza(name, price);
    }


    //credit card generation
    private CreditCard makeCard() {

        String num = switch(cardNum) {
            case VALID -> "1111111111111111";
            case SHORT -> "1111111";
            case LONG -> "111111111111111111";
            case NON_NUMERIC -> "1111abcd1111abcd";
        };

        String cvvVal = switch(cvv) {
            case VALID -> "382";
            case SHORT -> "12";
            case LONG -> "3821";
            case NON_NUMERIC -> "3a2";
        };

        String expiryVal = switch(expiry){
            case VALID -> "05/26";
            case EXPIRED -> "05/20";
            case INVALID -> "AAA";
        };

        return new CreditCard(num, expiryVal, cvvVal);
    }


    private int correctTotal(List<Pizza> pizzas) {
        return pizzas.stream().mapToInt(p -> p.priceInPence).sum() + 100;
    }

    private int makeTotal(int correct) {
        return switch(total) {
            case CORRECT -> correct;
            case LOW -> correct - 100;
            case HIGH -> correct + 100;
        };
    }

    private String makeDate() {
        return orderTime == OrderTime.OPEN ? "2025-12-05" : "2025-12-06"; // closed day
    }

    public String buildJson() {

        List<Pizza> pizzas = makePizzas();
        CreditCard card = makeCard();

        int correct = correctTotal(pizzas);
        int finalTotal = makeTotal(correct);

        return JsonBuilder.build(
                makeDate(),
                pizzas,
                card,
                finalTotal
        );
    }

    public Map<String,String> asStringMap() {
        Map<String,String> m = new HashMap<>();

        m.put("CARD", cardNum.name());
        m.put("CVV", cvv.name());
        m.put("EXPIRY", expiry.name());
        m.put("COUNT", pizzaCount.name());
        m.put("REST", restaurant.name());
        m.put("DEF", pizzaDef.name());
        m.put("PRICE", pizzaPrice.name());
        m.put("TOTAL", total.name());
        m.put("TIME", orderTime.name());

        return m;
    }


}

