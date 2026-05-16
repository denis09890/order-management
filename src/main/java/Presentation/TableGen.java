package Presentation;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableGen {
    public JTable createTable(List<?> objects) {
        if (objects == null || objects.isEmpty()) {
            return new JTable(new DefaultTableModel());
        }

        Field[] fields = objects.get(0).getClass().getDeclaredFields();
        Vector<String> columnNames = new Vector<>();
        for (Field field : fields) {
            columnNames.add(field.getName());
        }

        Vector<Vector<Object>> data = new Vector<>();
        for (Object obj : objects) {
            Vector<Object> row = new Vector<>();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    row.add(field.get(obj));
                } catch (Exception e) { e.printStackTrace(); }
            }
            data.add(row);
        }
        return new JTable(new DefaultTableModel(data, columnNames));
    }
}