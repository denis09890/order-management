package BusinessLogic;

import BusinessLogic.Validators.ProductStockValidator;
import BusinessLogic.Validators.Validator;
import DataAccess.ProductDAO;
import Model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business Logic Layer for Product operations.
 */
public class ProductBLL {
    private List<Validator<Product>> validators;
    private ProductDAO productDAO;

    public ProductBLL() {
        validators = new ArrayList<>();
        validators.add(new ProductStockValidator());
        productDAO = new ProductDAO();
    }

    /**
     * Finds a product by its unique ID.
     * @param id The ID of the product.
     * @return The found Product object.
     */
    public Product findProductById(int id) {
        Product p = productDAO.findById(id);
        if (p == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        return p;
    }

    /**
     * Retrieves all products from the database.
     * @return A list of all products.
     */
    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Validates and inserts a new product.
     * @param product The product data to insert.
     * @return The inserted Product object.
     */
    public Product insertProduct(Product product) {
        validators.forEach(v -> v.validate(product));
        return productDAO.insert(product);
    }

    /**
     * Validates and updates an existing product's stock data.
     * @param product The product data to update.
     */
    public void updateProduct(Product product) {
        validators.forEach(v -> v.validate(product));
        productDAO.update(product);
    }

    /**
     * Deletes a product from the database by ID.
     * @param id The ID of the product to delete.
     */
    public void deleteProduct(int id) {
        productDAO.delete(id);
    }
}