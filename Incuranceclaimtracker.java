import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InsuranceClaimTracker extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/insurance_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Change if you have a password

    private JTextField policyNoField, nameField, amountField, statusField;
    private JTextArea displayArea;
    private JButton addButton, viewButton, clearButton;

    public InsuranceClaimTracker() {
        setTitle("Insurance Claim Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        

        // === MAIN CONTAINER ===
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === INPUT PANEL ===
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Claim Details"));

        inputPanel.add(new JLabel("Policy Number:"));
        policyNoField = new JTextField();
        inputPanel.add(policyNoField);

        inputPanel.add(new JLabel("Claimant Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Claim Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Claim Status:"));
        statusField = new JTextField();
        inputPanel.add(statusField);

        // === BUTTON PANEL ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        addButton = new JButton("Add Claim");
        viewButton = new JButton("View All Claims");
        clearButton = new JButton("Clear Display");
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(clearButton);

        // === DISPLAY AREA ===
        displayArea = new JTextArea(15, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Claim Records"));

        // === ADD TO MAIN PANEL ===
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);

        add(mainPanel);

        // === ACTIONS ===
        addButton.addActionListener(e -> addClaim());
        viewButton.addActionListener(e -> viewClaims());
        clearButton.addActionListener(e -> displayArea.setText(""));

        setVisible(true);
    }

    private void addClaim() {
        String policyNo = policyNoField.getText().trim();
        String name = nameField.getText().trim();
        String amountText = amountField.getText().trim();
        String status = statusField.getText().trim();

        if (policyNo.isEmpty() || name.isEmpty() || amountText.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Please fill all fields.");
            return;
        }

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO claims (policy_number, claimant_name, amount, status) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, policyNo);
            ps.setString(2, name);
            ps.setDouble(3, Double.parseDouble(amountText));
            ps.setString(4, status);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Claim added successfully!");

            // Clear fields
            policyNoField.setText("");
            nameField.setText("");
            amountField.setText("");
            statusField.setText("");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Database error:\n" + ex.getMessage());
        }
    }

    private void viewClaims() {
        displayArea.setText("");
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM claims");

            displayArea.append(String.format("%-5s %-15s %-20s %-10s %-10s\n",
                    "ID", "Policy No", "Name", "Amount", "Status"));
            displayArea.append("---------------------------------------------------------------\n");

            boolean found = false;
            while (rs.next()) {
                found = true;
                displayArea.append(String.format("%-5d %-15s %-20s %-10.2f %-10s\n",
                        rs.getInt("id"),
                        rs.getString("policy_number"),
                        rs.getString("claimant_name"),
                        rs.getDouble("amount"),
                        rs.getString("status")));
            }

            if (!found) {
                displayArea.append("\nNo claims found in the database.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error retrieving data:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "❌ JDBC Driver not found!");
            return;
        }

        SwingUtilities.invokeLater(InsuranceClaimTracker::new);
    }
}
