package Presentation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Dynamic JTable UI builder utilizing Java Reflection metadata.
 */
public class TableGen {
    /**
     * Generates a structural JTable component matching any object list properties.
     * @param objects The source list of entity instances (Client, Product, Orders, Bill).
     * @return A visual JTable populated with data.
     */
    public JTable createTable(List<?> objects) {
        if (objects == null || objects.isEmpty()) {
            return new JTable(new DefaultTableModel());
        }

        Class<?> clazz = objects.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();
        Vector<String> columnNames = Arrays.stream(fields)
                .map(field -> field.getName().toUpperCase())
                .collect(Collectors.toCollection(Vector::new));

        Vector<Vector<Object>> data = new Vector<>();
        for (Object obj : objects) {
            Vector<Object> row = new Vector<>();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (clazz.isRecord()) {
                        Method accessorMethod = clazz.getMethod(field.getName());
                        row.add(accessorMethod.invoke(obj));
                    } else {
                        row.add(field.get(obj));
                    }
                } catch (Exception e) {
                    row.add(null);
                }
            }
            data.add(row);
        }
        return new JTable(new DefaultTableModel(data, columnNames));
    }
}