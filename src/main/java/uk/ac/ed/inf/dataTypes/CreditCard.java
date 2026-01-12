package uk.ac.ed.inf.dataTypes;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CreditCard {

    private String creditCardNumber;
    private String creditCardExpiry;
    private String cvv;

    public CreditCard(){};

    public CreditCard(String num, String expiryVal, String cvvVal) {
        this.creditCardNumber = num;
        this.creditCardExpiry = expiryVal;
        this.cvv = cvvVal;
    }

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
            return OrderValidationCode.EXPIRY_DATE_INVALID;

        } else if (creditCardNumber.length() != 16 || !creditCardNumber.chars().allMatch(Character::isDigit)) {
            return OrderValidationCode.CARD_NUMBER_INVALID;

        } else if (cvv.length() != 3 || !cvv.chars().allMatch(Character::isDigit)){
            return OrderValidationCode.CVV_INVALID;

        } else {
            return OrderValidationCode.UNDEFINED;
        }
    }
}
