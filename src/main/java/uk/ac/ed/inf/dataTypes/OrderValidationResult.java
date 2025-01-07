package uk.ac.ed.inf.dataTypes;

public class OrderValidationResult {

    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;

    public OrderValidationResult(OrderStatus orderStatus, OrderValidationCode orderValidationCode) {
        this.orderStatus = orderStatus;
        this.orderValidationCode = orderValidationCode;
    }

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

    @Override
    public boolean equals(Object obj){
        if(obj instanceof OrderValidationResult){
            OrderValidationResult other = (OrderValidationResult)obj;
            return this.orderStatus.equals(other.orderStatus) && this.orderValidationCode.equals(other.orderValidationCode);
        }
        return false;
    }


}
