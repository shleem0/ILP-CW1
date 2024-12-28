package uk.ac.ed.inf.dataTypes;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CreditCard {

    private String creditCardNumber;
    private String creditCardExpiry;
    private String cvv;

    public String getCreditCardNumber(){
        return creditCardNumber;
    }
    public void setCreditCardNumber(String creditCardNumber){
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardExpiry(){
        return creditCardExpiry;
    }
    public void setCreditCardExpiry(String creditCardExpiry){
        this.creditCardExpiry = creditCardExpiry;
    }

    public String getCVV(){
        return cvv;
    }
    public void setCVV(String cvv){
        this.cvv = cvv;
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

        } else if (creditCardNumber.length() != 16 || !creditCardNumber.chars().allMatch(Character::isDigit)) {
            System.out.println(creditCardNumber);
            System.out.println("Invalid card");
            return OrderValidationCode.CARD_NUMBER_INVALID;

        } else if (cvv.length() != 3 || !cvv.chars().allMatch(Character::isDigit)){
            System.out.println("Invalid cvv");
            return OrderValidationCode.CVV_INVALID;

        } else {
            return OrderValidationCode.UNDEFINED;
        }
    }
}
