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

    public OrderValidationCode validateCreditCard(YearMonth orderDate){

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiry = YearMonth.parse(creditCardExpiry, df);

        if (expiry.isBefore(orderDate)) {
            return OrderValidationCode.EXPIRY_DATE_INVALID;
        }
        else if (creditCardNumber.length() != 16){
            System.out.println(creditCardNumber);
            return OrderValidationCode.CARD_NUMBER_INVALID;
        }
        else if (cvv.length() != 3){
            return OrderValidationCode.CVV_INVALID;
        }
        else{
            return OrderValidationCode.UNDEFINED;
        }
    }
}
