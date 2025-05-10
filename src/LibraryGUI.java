import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private static final String PASSWORD = "Ch1ttm$bi"; // Replace with your actual password

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel panel1 = new JPanel(new BorderLayout());

        tabbedPane.add("Book Search and Availability", panel1);
        JPanel panel2 = new JPanel(new BorderLayout());
        tabbedPane.add("Book Loans", panel2);

        JPanel panel3 = new JPanel(new BorderLayout());
        tabbedPane.add("Borrower Management", panel3);
        JPanel panel4 = new JPanel(new BorderLayout());
        tabbedPane.add("Fines", panel4);

        // Create a panel for user input
        JPanel panel = new JPanel(new BorderLayout());


        // Add a label and text field for search input
        JLabel searchLabel = new JLabel("Enter search term:");
        JTextField searchField = new JTextField("", 80);

        JButton searchButton = new JButton("Search");


        searchLabel.setSize(50,15);
        searchField.setMinimumSize(new Dimension(300, 20));
        searchButton.setSize(50,15);

        // Add components to the panel

        panel.add(searchLabel, BorderLayout.WEST);

        panel.add(searchField, BorderLayout.CENTER);

        panel.add(searchButton, BorderLayout.EAST);
        panel.setSize(800, 100);
        panel1.add(panel, BorderLayout.NORTH);
        // Add the panel to the frame
        //frame.add(panel, BorderLayout.NORTH);
        frame.add(tabbedPane, BorderLayout.CENTER);
        JTable resultTable = new JTable();
        // Create a text area to display results
        JTextArea resultArea = new JTextArea();

        resultArea.setEditable(false);

        // Set up the database connection
        Connection conn;
        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to connect to the database. Error:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JScrollPane scrollPane = new JScrollPane(resultTable);
        panel1.add(scrollPane, BorderLayout.CENTER);
        JLabel noDataLabel = new JLabel("No record found", SwingConstants.CENTER);
        resultTable.setModel(new DefaultTableModel(new String[][]{}, ListRowBook.getHeaderColumnNames()));

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                if (searchTerm.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a search term.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                resultTable.setModel(new DefaultTableModel(new String[][]{}, ListRowBook.getHeaderColumnNames()));
                try {
                    // Call the search method and display results
                    ArrayList<ListRow> searchResult = LibraryManagement.search(conn, searchTerm);
                    if (searchResult.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "No Records Found for the search criteria", "No Records Found", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        // Append the header to the result area
                        String[][] tableData = new String[searchResult.size()][5];
                        int index = 0;
                        for (ListRow row : searchResult) {
                            tableData[index] = row.getColumnsValues();
                            index++;
                        }

                        resultTable.setModel(new DefaultTableModel(tableData, ListRowBook.getHeaderColumnNames()));
                        noDataLabel.setVisible(false);
                        resultTable.setVisible(true);
                    }
                    panel1.revalidate();
                    panel1.repaint();
                    frame.repaint();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error executing search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }
}