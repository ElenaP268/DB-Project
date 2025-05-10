import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.System.exit;

public class LibraryGUI {

    private static Connection conn;
    public static void main(String[] args) {
        JFrame frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        // Set up the database connection

        try {
            conn = DriverManager.getConnection(Config.DB_URL, Config.USERNAME, Config.PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to connect to the database. Error:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            exit(999);
        }

        // Create the main frame


        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel panel1 = new JPanel(new BorderLayout());
        LibraryGUI libraryGUI = new LibraryGUI();

        tabbedPane.add("Book Search and Availability", panel1);
        JPanel panel2 = new JPanel();
        tabbedPane.add("Book Loans", panel2);
        BookLoansGui bookLoansGui = new BookLoansGui(panel2, conn);

        JPanel panel3 = new JPanel(new BorderLayout());
        tabbedPane.add("Borrower Management", panel3);
        BorrowerManagementGUI borrowerManagementGUI = new BorrowerManagementGUI(panel3, conn);

        JPanel panel4 = new JPanel(new BorderLayout());
        tabbedPane.add("Fines", panel4);
        FinesGUI finesGUI = new FinesGUI(panel4, conn);
        frame.add(tabbedPane, BorderLayout.CENTER);



        libraryGUI.show(panel1);
        // Make the frame visible
        frame.setVisible(true);
        frame.repaint();
    }

    public void show(JPanel mainPanel) {
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
        mainPanel.add(panel, BorderLayout.NORTH);
        // Add the panel to the frame
        //frame.add(panel, BorderLayout.NORTH);

        JTable resultTable = new JTable();
        // Create a text area to display results
        JTextArea resultArea = new JTextArea();

        resultArea.setEditable(false);


        JScrollPane scrollPane = new JScrollPane(resultTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JLabel noDataLabel = new JLabel("No record found", SwingConstants.CENTER);
        resultTable.setModel(new DefaultTableModel(new String[][]{}, ListRowBook.getHeaderColumnNames()));
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel footerPanel = new JPanel(new GridLayout(1,3));
        JButton checkoutButton = new JButton("Checkout Selected Book");
        checkoutButton.setMaximumSize(new Dimension(100, 20));
        footerPanel.add(new JPanel());
        footerPanel.add(checkoutButton);
        footerPanel.add(new JPanel());
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(resultTable.getSelectedRow() >= 0) {
                    String status = resultTable.getValueAt(resultTable.getSelectedRow(), 3).toString();
                    if (status.equalsIgnoreCase("Available")) {
                        String isbn =  resultTable.getValueAt(resultTable.getSelectedRow(), 0).toString();
                        try {
                            LibraryManagement.checkoutFromIsbn(conn, isbn, mainPanel);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(mainPanel, "Failed to do database operation. Error:" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "The selected book is not available for checkout.", "Book Not Available", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Please select a book to checkout.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                if (searchTerm.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Please enter a search term.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                resultTable.setModel(new DefaultTableModel(new String[][]{}, ListRowBook.getHeaderColumnNames()));
                try {
                    // Call the search method and display results
                    ArrayList<ListRow> searchResult = LibraryManagement.search(conn, searchTerm);
                    if (searchResult.isEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, "No Records Found for the search criteria", "No Records Found", JOptionPane.INFORMATION_MESSAGE);

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
                    mainPanel.revalidate();
                    mainPanel.repaint();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Error executing search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


    }
}