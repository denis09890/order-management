package DataAccess;

import Connection.ConnectionFactory;
import Model.Bill;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillDAO {
    private static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());

    private static final String INSERT_QUERY = "INSERT INTO bill (order_id, client_name, product_name, quantity, orderTime) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM bill";

    public void insert(Bill bill) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(INSERT_QUERY);

            statement.setString(1, bill.client_name());
            statement.setString(2, bill.product_name());
            statement.setInt(3, bill.quantity());
            statement.setTimestamp(4, Timestamp.valueOf(bill.orderTime()));

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    public List<Bill> findAll() {
        List<Bill> billList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(SELECT_ALL_QUERY);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                String clientName = resultSet.getString("client_name");
                String productName = resultSet.getString("product_name");
                int quantity = resultSet.getInt("quantity");
                LocalDateTime orderTime = resultSet.getTimestamp("orderTime").toLocalDateTime();

                billList.add(new Bill(orderId, clientName, productName, quantity, orderTime));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return billList;
    }
}