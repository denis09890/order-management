package BusinessLogic.Validators;

import Model.Orders;

public class OrderQuantityValidator implements Validator<Orders>{
    public void validate(Orders o) {
        if (o.getQuantity() <= 0) {
            throw new IllegalArgumentException("The quantity must be more than 0!");
        }
    }
}