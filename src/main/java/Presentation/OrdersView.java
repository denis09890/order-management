package Presentation;

import BusinessLogic.ClientBLL;
import BusinessLogic.OrdersBLL;
import BusinessLogic.ProductBLL;
import DataAccess.BillDAO;
import Model.Orders;
import javax.swing.*;
import java.awt.*;

/**
 * User interface window for placing orders and viewing checkout logs.
 */
public class OrdersView extends JFrame {
    private OrdersBLL orderBLL = new OrdersBLL();
    private ClientBLL clientBLL = new ClientBLL();
    private ProductBLL productBLL = new ProductBLL();
    private TableGen tableGenerator = new TableGen();

    private JComboBox<String> clientCombo = new JComboBox<>();
    private JComboBox<String> productCombo = new JComboBox<>();
    private JTextField quantityField = new JTextField();
    private JScrollPane tableScroll = new JScrollPane();

    public OrdersView() {
        this.setTitle("Create New Order");
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        updateCombos();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(new JLabel("Select Client:")); inputPanel.add(clientCombo);
        inputPanel.add(new JLabel("Select Product:")); inputPanel.add(productCombo);
        inputPanel.add(new JLabel("Desired Quantity:")); inputPanel.add(quantityField);

        JButton orderBtn = new JButton("PLACE ORDER");
        orderBtn.setFont(new Font("Arial", Font.BOLD, 14));
        orderBtn.setBackground(new Color(100, 200, 100));

        JButton viewBillsBtn = new JButton("View Order History (Bills)");
        viewBillsBtn.addActionListener(e -> showBillLog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewBillsBtn);
        buttonPanel.add(orderBtn);

        orderBtn.addActionListener(e -> {
            try {
                int clientId = Integer.parseInt(clientCombo.getSelectedItem().toString().split(" - ")[0]);
                int productId = Integer.parseInt(productCombo.getSelectedItem().toString().split(" - ")[0]);
                int qty = Integer.parseInt(quantityField.getText());

                orderBLL.createOrder(new Orders(clientId, productId, qty));
                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Order Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.NORTH);
        this.add(tableScroll, BorderLayout.CENTER);

        refreshTable();
        this.setLocationRelativeTo(null);
    }
    private void updateCombos() {
        clientCombo.removeAllItems();
        productCombo.removeAllItems();
        clientBLL.findAllClients().forEach(c -> clientCombo.addItem(c.getId() + " - " + c.getName()));
        productBLL.findAllProducts().forEach(p -> productCombo.addItem(p.getId() + " - " + p.getName() + " (Stock: " + p.getQuantity() + ")"));
    }

    private void refreshTable() {
        tableScroll.setViewportView(tableGenerator.createTable(orderBLL.findAllOrders()));
        updateCombos();
    }

    private void showBillLog() {
        JFrame logFrame = new JFrame("Order History (Bills Log)");
        logFrame.setSize(750, 450);
        logFrame.setLayout(new BorderLayout());
        logFrame.setLocationRelativeTo(this);

        BillDAO billDAO = new BillDAO();
        java.util.List<Model.Bill> allBills = billDAO.findAll();

        if (allBills.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Doesn t exist any bill!", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTable billTable = tableGenerator.createTable(allBills);
        JScrollPane scrollPane = new JScrollPane(billTable);

        logFrame.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close History");
        closeBtn.addActionListener(e -> logFrame.dispose());
        JPanel southPanel = new JPanel();
        southPanel.add(closeBtn);
        logFrame.add(southPanel, BorderLayout.SOUTH);

        logFrame.setVisible(true);
    }
}