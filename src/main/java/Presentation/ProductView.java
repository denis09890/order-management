package Presentation;

import BusinessLogic.ProductBLL;
import Model.Product;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductView extends JFrame {
    private ProductBLL productBLL = new ProductBLL();
    private TableGen tableGenerator = new TableGen();
    private JTable table;

    private JTextField idField = new JTextField();
    private JTextField nameField = new JTextField();
    private JTextField quantityField = new JTextField();
    private JScrollPane tableScroll = new JScrollPane();

    public ProductView() {
        this.setTitle("Product Management");
        this.setSize(800, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.add(new JLabel("Product ID:")); inputPanel.add(idField);
        inputPanel.add(new JLabel("Product Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Stock Quantity:")); inputPanel.add(quantityField);

        JButton addBtn = new JButton("Add Product");
        JButton editBtn = new JButton("Update Stock");
        JButton deleteBtn = new JButton("Remove Product");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn); buttonPanel.add(editBtn); buttonPanel.add(deleteBtn);

        addBtn.addActionListener(e -> {
            try {
                productBLL.insertProduct(new Product(nameField.getText(), Integer.parseInt(quantityField.getText())));
                refreshTable();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        editBtn.addActionListener(e -> {
            try {
                Product p = new Product(nameField.getText(), Integer.parseInt(quantityField.getText()));
                p.setId(Integer.parseInt(idField.getText()));
                productBLL.updateProduct(p);
                refreshTable();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Select a product!"); }
        });

        deleteBtn.addActionListener(e -> {
            try {
                productBLL.deleteProduct(Integer.parseInt(idField.getText()));
                refreshTable();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Select a product!"); }
        });

        this.add(inputPanel, BorderLayout.NORTH);
        this.add(tableScroll, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        refreshTable();
    }

    private void refreshTable() {
        List<Product> products = productBLL.findAllProducts();
        table = tableGenerator.createTable(products);
        tableScroll.setViewportView(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                idField.setText(table.getValueAt(row, 0).toString());
                nameField.setText(table.getValueAt(row, 1).toString());
                quantityField.setText(table.getValueAt(row, 2).toString());
            }
        });
    }
}