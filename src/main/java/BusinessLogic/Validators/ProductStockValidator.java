package BusinessLogic.Validators;

import Model.Product;

/**
 * Validates the stock quantity of a product.
 */
public class ProductStockValidator implements Validator<Product> {
    /**
     * Checks if the stock quantity is not negative.
     * @param p The product to validate.
     */
    public void validate(Product p) {
        if (p.getQuantity() < 0) {
            throw new IllegalArgumentException("The value of quantity less than zero.!");
        }
    }
}