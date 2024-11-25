package uk.ac.ed.inf.dataTypes;

public class OrderValidationResult {

    public OrderStatus orderStatus;
    public OrderValidationCode orderValidationCode;


    public OrderStatus getOrderStatus(){
        return orderStatus;
    }

    public OrderValidationCode getOrderValidationCode(){
        return orderValidationCode;
    }

    public void setStatus(OrderStatus status){
        this.orderStatus = status;
    }
    public void setValidationCode(OrderValidationCode code){
        this.orderValidationCode = code;
    }

}
