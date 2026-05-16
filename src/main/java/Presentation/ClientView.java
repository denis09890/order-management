package Presentation;

import BusinessLogic.ClientBLL;
import Model.Client;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class ClientView extends JFrame {
    private ClientBLL clientBLL = new ClientBLL();
    private TableGen tableGenerator = new TableGen();
    private JTable table;

    private JTextField idField = new JTextField();
    private JTextField nameField = new JTextField();
    private JTextField addressField = new JTextField();
    private JTextField emailField = new JTextField();
    private JTextField ageField = new JTextField();
    private JScrollPane tableScroll = new JScrollPane();

    public ClientView() {
        this.setTitle("Client Management");
        this.setSize(900, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.add(new JLabel("ID (auto-selected):")); inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Address:")); inputPanel.add(addressField);
        inputPanel.add(new JLabel("Email:")); inputPanel.add(emailField);
        inputPanel.add(new JLabel("Age:")); inputPanel.add(ageField);

        JButton addBtn = new JButton("Add New");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton viewBtn = new JButton("Refresh Table");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewBtn); buttonPanel.add(addBtn);
        buttonPanel.add(editBtn); buttonPanel.add(deleteBtn);

        addBtn.addActionListener(e -> {
            try {
                Client c = new Client(nameField.getText(), addressField.getText(), emailField.getText(), Integer.parseInt(ageField.getText()));
                clientBLL.insertClient(c);
                refreshTable();
                clearFields();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        editBtn.addActionListener(e -> {
            try {
                Client c = new Client(nameField.getText(), addressField.getText(), emailField.getText(), Integer.parseInt(ageField.getText()));
                c.setId(Integer.parseInt(idField.getText()));
                clientBLL.updateClient(c);
                refreshTable();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Select a client first!"); }
        });

        deleteBtn.addActionListener(e -> {
            try {
                clientBLL.deleteClient(Integer.parseInt(idField.getText()));
                refreshTable();
                clearFields();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Select a client to delete!"); }
        });

        viewBtn.addActionListener(e -> refreshTable());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(tableScroll, BorderLayout.CENTER);
        refreshTable();
        this.setLocationRelativeTo(null);
    }

    private void refreshTable() {
        List<Client> clients = clientBLL.findAllClients();
        table = tableGenerator.createTable(clients);
        tableScroll.setViewportView(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                idField.setText(table.getValueAt(row, 0).toString());
                nameField.setText(table.getValueAt(row, 1).toString());
                addressField.setText(table.getValueAt(row, 2).toString());
                emailField.setText(table.getValueAt(row, 3).toString());
                ageField.setText(table.getValueAt(row, 4).toString());
            }
        });
    }

    private void clearFields() {
        idField.setText(""); nameField.setText(""); addressField.setText(""); emailField.setText(""); ageField.setText("");
    }
}