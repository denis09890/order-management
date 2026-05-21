package DataAccess;

import Connection.ConnectionFactory;
import Model.Bill;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object specialized for managing Bill records manually.
 */
public class BillDAO {
    private static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());

    private static final String INSERT_QUERY = "INSERT INTO bill (client_name, product_name, quantity, ordertime) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM bill";

    /**
     * Inserts a Bill log into the database using explicit SQL.
     * @param bill The immutable bill data record.
     */
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

    /**
     * Retrieves all bill records from the bill log table.
     * @return A list of Bill records.
     */
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
                int billId = resultSet.getInt("bill_id");
                String clientName = resultSet.getString("client_name");
                String productName = resultSet.getString("product_name");
                int quantity = resultSet.getInt("quantity");
                LocalDateTime orderTime = resultSet.getTimestamp("ordertime").toLocalDateTime();

                billList.add(new Bill(billId, clientName, productName, quantity, orderTime));
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