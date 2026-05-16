package BusinessLogic;

import BusinessLogic.Validators.OrderQuantityValidator;
import BusinessLogic.Validators.Validator;
import DataAccess.BillDAO;
import DataAccess.ClientDAO;
import DataAccess.OrdersDAO;
import DataAccess.ProductDAO;
import Model.Bill;
import Model.Client;
import Model.Orders;
import Model.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdersBLL {
    private List<Validator<Orders>> validators;
    private OrdersDAO orderDAO;
    private ProductDAO productDAO;
    private ClientDAO clientDAO;

    public OrdersBLL() {
        validators = new ArrayList<>();
        validators.add(new OrderQuantityValidator());
        orderDAO = new OrdersDAO();
        productDAO = new ProductDAO();
        clientDAO = new ClientDAO();
    }

    public void createOrder(Orders order) {
        for (Validator<Orders> v : validators) {
            v.validate(order);
        }

        Product product = productDAO.findById(order.getId_product());
        if (product == null) {
            throw new IllegalArgumentException("Product not found!");
        }

        if (product.getQuantity() < order.getQuantity()) {
            throw new IllegalArgumentException("Under-stock! Only " + product.getQuantity() + " items available.");
        }

        Client client = clientDAO.findById(order.getId_client());
        if (client == null) {
            throw new IllegalArgumentException("Client not found!");
        }

        product.setQuantity(product.getQuantity() - order.getQuantity());
        productDAO.update(product);

        orderDAO.insert(order);

        Bill bill = new Bill(
                0,
                client.getName(),
                product.getName(),
                order.getQuantity(),
                LocalDateTime.now()
        );

        new BillDAO().insert(bill);
    }

    public List<Orders> findAllOrders() {
        return orderDAO.findAll();
    }
}