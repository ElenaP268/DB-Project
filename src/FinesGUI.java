import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class FinesGUI {
    private Connection conn;
    private JPanel finesGui;

    public FinesGUI(JPanel jPanel, Connection conn) {
        this.finesGui = jPanel;
        this.conn = conn;

        try {
            loadAllControls();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        finesGui.repaint();
    }

    private void loadAllControls() throws ParseException {
        // Add button to show fines
        JPanel panel = new JPanel(new BorderLayout());
        JButton searchButton = new JButton("View Current Fines");
        searchButton.setSize(50,15);
        panel.add(searchButton, BorderLayout.WEST);
        JButton updateButton = new JButton("Update Fines (End of Day)");
        searchButton.setSize(50,15);
        panel.add(updateButton, BorderLayout.EAST);
        panel.setSize(800, 100);
        finesGui.add(panel, BorderLayout.NORTH);

        // Create a text area to display results
        JTable resultTable = new JTable();
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);


        JScrollPane scrollPane = new JScrollPane(resultTable);
        finesGui.add(scrollPane, BorderLayout.CENTER);
        JLabel noDataLabel = new JLabel("No fines found", SwingConstants.CENTER);
        resultTable.setModel(new DefaultTableModel(new String[][]{}, ListRowFine.getHeaderColumnNames()));
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel footerPanel = new JPanel(new GridLayout(1,3));
        JButton checkoutButton = new JButton("Pay Selected Fine");
        checkoutButton.setMaximumSize(new Dimension(100, 20));
        footerPanel.add(new JPanel());
        footerPanel.add(checkoutButton);
        footerPanel.add(new JPanel());
        
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(resultTable.getSelectedRow() >= 0) {
                        String cardID = resultTable.getValueAt(resultTable.getSelectedRow(), 0).toString();
                        try {
                            LibraryManagement.payFines(conn, cardID, finesGui);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(finesGui, "Failed to do database operation. Error:" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                } else {
                    JOptionPane.showMessageDialog(finesGui, "Please select a borrower.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        finesGui.add(footerPanel, BorderLayout.SOUTH);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultTable.setModel(new DefaultTableModel(new String[][]{}, ListRowFine.getHeaderColumnNames()));
                try {
                    // Call the search method and display results
                    ArrayList<ListRow> searchResult = LibraryManagement.displayFines(conn);
                    if (searchResult.isEmpty()) {
                        JOptionPane.showMessageDialog(finesGui, "No fines found.", "No Records Found", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        // Append the header to the result area
                        String[][] tableData = new String[searchResult.size()][2];
                        int index = 0;
                        for (ListRow row : searchResult) {
                            tableData[index] = row.getColumnsValues();
                            index++;
                        }

                        resultTable.setModel(new DefaultTableModel(tableData, ListRowFine.getHeaderColumnNames()));
                        noDataLabel.setVisible(false);
                        resultTable.setVisible(true);
                    }
                    finesGui.revalidate();
                    finesGui.repaint();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(finesGui, "Error executing search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LibraryManagement.updateFines(conn);
                    searchButton.doClick();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(finesGui, "Failed to do database operation. Error:" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }

    }
