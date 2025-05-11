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
import java.text.ParseException;
import java.util.ArrayList;

public class BookLoansGui {

    private Connection conn;
    private JPanel bookLoansPanel;

    public BookLoansGui(JPanel jPanel, Connection conn) {
        this.bookLoansPanel = jPanel;
        this.conn = conn;

        show(bookLoansPanel);
        bookLoansPanel.repaint();
    }

    public void show(JPanel mainPanel) {
        // Create a panel for user input
        JPanel panel = new JPanel(new BorderLayout());


        // Add a label and text field for search input
        JLabel searchLabel = new JLabel("Enter borrower ID:");
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
        resultTable.setModel(new DefaultTableModel(new String[][]{}, ListRowLoan.getHeaderColumnNames()));
        resultTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel footerPanel = new JPanel(new GridLayout(1,3));
        JButton checkinButton = new JButton("Checkin Selected Book");
        checkinButton.setMaximumSize(new Dimension(100, 20));
        footerPanel.add(new JPanel());
        footerPanel.add(checkinButton);
        footerPanel.add(new JPanel());
        checkinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(resultTable.getSelectedRow() >= 0) {
                    boolean checkinSuccess = true;
                    for (int i = 0; i < resultTable.getSelectedRowCount() && i < 4 && checkinSuccess; i++) {
                        String loanID = resultTable.getValueAt(resultTable.getSelectedRows()[i], 0).toString();
                        try {
                            checkinSuccess = LibraryManagement.checkIn(conn, loanID, mainPanel);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(mainPanel, "Failed to do database operation. Error:" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Please select a book to checkin.", "Warning", JOptionPane.WARNING_MESSAGE);
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
                resultTable.setModel(new DefaultTableModel(new String[][]{}, ListRowLoan.getHeaderColumnNames()));
                try {
                    // Call the search method and display results
                    ArrayList<ListRow> searchResult = LibraryManagement.loanSearch(conn, searchTerm);
                    if (searchResult.isEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, "Borrower has no active loans.", "No Records Found", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        // Append the header to the result area
                        String[][] tableData = new String[searchResult.size()][5];
                        int index = 0;
                        for (ListRow row : searchResult) {
                            tableData[index] = row.getColumnsValues();
                            index++;
                        }

                        resultTable.setModel(new DefaultTableModel(tableData, ListRowLoan.getHeaderColumnNames()));
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
