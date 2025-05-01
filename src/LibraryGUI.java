import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class LibraryGUI {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password"; // Replace with your actual password

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create a panel for user input
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        // Add a label and text field for search input
        JLabel searchLabel = new JLabel("Enter search term:");
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");

        // Add components to the panel
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);

        // Add the panel to the frame
        frame.add(panel, BorderLayout.NORTH);

        // Create a text area to display results
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Set up the database connection
        Connection conn;
        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                if (searchTerm.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a search term.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
        
                try {
                    // Call the search method and display results
                    ArrayList<ListRow> searchResult = LibraryManagement.search(conn, searchTerm);
                    resultArea.setText(""); // Clear previous results
                    if (searchResult.isEmpty()) {
                        resultArea.append("No records found.\n");
                    } else {
                        // Append the header to the result area
                        resultArea.append(((ListRowBook) searchResult.get(0)).getHeader() + "\n");
                        for (ListRow row : searchResult) {
                            resultArea.append(row.toString() + "\n");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error executing search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }
}