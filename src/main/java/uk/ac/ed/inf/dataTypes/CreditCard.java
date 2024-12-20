package uk.ac.ed.inf.dataTypes;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CreditCard {

    public String creditCardNumber;
    public String creditCardExpiry;
    public String cvv;

    public String getNumber(){
        return creditCardNumber;
    }

    public String getExpiry(){
        return creditCardExpiry;
    }

    public String getCVV(){
        return cvv;
    }

    public OrderValidationCode validateCreditCard(YearMonth orderDate) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiry;

        try {
            expiry = YearMonth.parse(creditCardExpiry, df);
        } catch (Exception e) {
            return OrderValidationCode.EXPIRY_DATE_INVALID;
        }

        if (expiry.isBefore(orderDate)) {
            System.out.println("Invalid expiry");
            return OrderValidationCode.EXPIRY_DATE_INVALID;
        } else if (creditCardNumber.length() != 16) {
            System.out.println(creditCardNumber);
            System.out.println("Invalid card");
            return OrderValidationCode.CARD_NUMBER_INVALID;
        } else if (cvv.length() != 3) {
            System.out.println("Invalid cvv");
            return OrderValidationCode.CVV_INVALID;
        } else {
            return OrderValidationCode.UNDEFINED;
        }
    }
}
