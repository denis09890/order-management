package Presentation;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    public MainView() {
        this.setTitle("Orders Management System");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(3, 1, 10, 10));

        JButton clientBtn = new JButton("Client Management");
        JButton productBtn = new JButton("Product Management");
        JButton orderBtn = new JButton("Create Order");

        clientBtn.addActionListener(e -> new ClientView().setVisible(true));

        productBtn.addActionListener(e -> new ProductView().setVisible(true));

        orderBtn.addActionListener(e -> new OrdersView().setVisible(true));

        this.add(clientBtn);
        this.add(productBtn);
        this.add(orderBtn);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new MainView().setVisible(true);
    }
}