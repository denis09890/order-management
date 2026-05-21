package BusinessLogic.Validators;

import Model.Orders;

/**
 * Validates the quantity of an order.
 */
public class OrderQuantityValidator implements Validator<Orders>{
    /**
     * Checks if the order quantity is greater than 0.
     * @param o The order to validate.
     */
    public void validate(Orders o) {
        if (o.getQuantity() <= 0) {
            throw new IllegalArgumentException("The quantity must be more than 0!");
        }
    }
}