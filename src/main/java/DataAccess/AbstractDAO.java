package DataAccess;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Connection.ConnectionFactory;

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
        Constructor<?> ctor = null;
        for (Constructor<?> c : ctors) {
            ctor = c;
            if (ctor.getParameterCount() == 0) break;
        }

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

    public T insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        StringBuilder sb = new StringBuilder();

        Field[] allFields = type.getDeclaredFields();
        List<Field> fieldsToInsert = new ArrayList<>();
        for (Field f : allFields) {
            if (!f.getName().equalsIgnoreCase("id")) {
                fieldsToInsert.add(f);
            }
        }

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

    public T update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        Field[] fields = type.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE \"").append(type.getSimpleName().toLowerCase()).append("\" SET ");

        try {
            for (int i = 0; i < fields.length; i++) {
                sb.append(fields[i].getName()).append(" = ?");
                if (i < fields.length - 1) sb.append(", ");
            }
            sb.append(" WHERE id = ?");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sb.toString());

            int index = 1;
            Object idValue = null;
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(t);
                statement.setObject(index++, value);
                if (field.getName().equals("id")) idValue = value;
            }
            statement.setObject(index, idValue);

            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }

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