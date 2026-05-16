package BusinessLogic.Validators;

import Model.Product;

public class ProductStockValidator implements Validator<Product> {
    public void validate(Product p) {
        if (p.getQuantity() < 0) {
            throw new IllegalArgumentException("The value of quantity less than zero.!");
        }
    }
}
