package DataAccess;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Connection.ConnectionFactory;

/**
 * Generic base Data Access Object class implementing CRUD operations via Java Reflection.
 * @param <T> The model entity type.
 */
public abstract class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append("\"").append(type.getSimpleName().toLowerCase()).append("\"");
        if (field != null) {
            sb.append(" WHERE ").append(field).append(" = ?");
        }
        return sb.toString();
    }

    /**
     * Fetches all records from the corresponding database table.
     * @return A list of populated objects.
     */
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery(null);
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return new ArrayList<>();
    }

    /**
     * Finds a single record by its ID.
     * @param id The target ID.
     * @return The populated entity object, or null if not found.
     */
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            List<T> list = createObjects(resultSet);
            if (!list.isEmpty()) {
                return list.get(0);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        Constructor<?>[] ctors = type.getDeclaredConstructors();
        Constructor<?> ctor = Arrays.stream(ctors)
                .filter(c -> c.getParameterCount() == 0)
                .findFirst()
                .orElse(null);

        try {
            while (resultSet.next()) {
                T instance = (T) ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = resultSet.getObject(field.getName());
                    field.set(instance, value);
                }
                list.add(instance);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating objects via reflection", e);
        }
        return list;
    }

    /**
     * Dynamically inserts a new object into the database.
     * @param t The object entity to store.
     * @return The inserted object.
     */
    public T insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        StringBuilder sb = new StringBuilder();

        Field[] allFields = type.getDeclaredFields();
        List<Field> fieldsToInsert = Arrays.stream(allFields)
                .filter(f -> !f.getName().equalsIgnoreCase("id"))
                .toList();

        sb.append("INSERT INTO \"").append(type.getSimpleName().toLowerCase()).append("\" (");
        for (int i = 0; i < fieldsToInsert.size(); i++) {
            sb.append(fieldsToInsert.get(i).getName());
            if (i < fieldsToInsert.size() - 1) sb.append(", ");
        }
        sb.append(") VALUES (");
        for (int i = 0; i < fieldsToInsert.size(); i++) {
            sb.append("?");
            if (i < fieldsToInsert.size() - 1) sb.append(", ");
        }
        sb.append(")");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sb.toString());
            for (int i = 0; i < fieldsToInsert.size(); i++) {
                fieldsToInsert.get(i).setAccessible(true);
                statement.setObject(i + 1, fieldsToInsert.get(i).get(t));
            }
            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }

    /**
     * Dynamically updates an existing record in the database.
     * @param t The updated object entity.
     * @return The updated entity instance.
     */
    public T update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        Field[] fields = type.getDeclaredFields();
        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE \"").append(type.getSimpleName().toLowerCase()).append("\" SET ");

        List<Field> fieldsToUpdate = Arrays.stream(fields)
                .filter(f -> !f.getName().equalsIgnoreCase("id"))
                .toList();

        for (int i = 0; i < fieldsToUpdate.size(); i++) {
            sb.append(fieldsToUpdate.get(i).getName()).append(" = ?");
            if (i < fieldsToUpdate.size() - 1) sb.append(", ");
        }
        sb.append(" WHERE id = ?");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sb.toString());

            int index = 1;
            for (Field field : fieldsToUpdate) {
                field.setAccessible(true);
                statement.setObject(index++, field.get(t));
            }

            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            statement.setObject(index, idField.get(t));

            statement.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }

    /**
     * Deletes a record from the database by its ID.
     * @param id The target record ID.
     */
    public void delete(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM ");
        sb.append("\"").append(type.getSimpleName().toLowerCase()).append("\"");
        sb.append(" WHERE id = ?");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sb.toString());
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                LOGGER.log(Level.INFO, type.getSimpleName() + " with id " + id + " was deleted successfully.");
            } else {
                LOGGER.log(Level.INFO, "No " + type.getSimpleName() + " found with id " + id);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }
}