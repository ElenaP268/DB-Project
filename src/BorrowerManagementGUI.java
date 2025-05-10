import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class BorrowerManagementGUI  {

    private Connection conn;
    private JPanel borrowerManagementPanel;

    public BorrowerManagementGUI(JPanel jPanel, Connection conn) {
        this.borrowerManagementPanel = jPanel;
        this.conn = conn;


    }

    public void show() {
        try {
            loadAllControls();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAllControls() throws ParseException {

        JLabel nameLabel = new JLabel("Name: ", SwingConstants.RIGHT);
        JLabel ssnLabel = new JLabel("SSN: ", SwingConstants.RIGHT);
        JLabel addressLabel = new JLabel("Address: ", SwingConstants.RIGHT);
        JLabel phoneLabel = new JLabel("Phone: ", SwingConstants.RIGHT);
        nameLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        ssnLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        addressLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        phoneLabel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JTextField nameField = new JTextField(50);
        JTextField addressField = new JTextField(50);
        nameField.setBorder(new LineBorder(Color.BLACK, 1));
        addressField.setBorder(new LineBorder(Color.BLACK, 1));

        MaskFormatter maskFormatter = new MaskFormatter("(###) ###-####");
        maskFormatter.setPlaceholderCharacter('_');
        JFormattedTextField phoneField = new JFormattedTextField(maskFormatter);
        phoneField.setColumns(14);
        phoneField.setBorder(new LineBorder(Color.BLACK, 1));

        MaskFormatter ssnMaskFormatter = new MaskFormatter("###-##-####");
        maskFormatter.setPlaceholderCharacter('_');
        JFormattedTextField ssnField = new JFormattedTextField(ssnMaskFormatter);
        ssnField.setColumns(11);
        ssnField.setBorder(new LineBorder(Color.BLACK, 1));

        JLabel titleLabel = new JLabel("BORROWER MANAGEMENT ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JButton addBorwowerButton = new JButton("Add Borrower");
        JButton resetBorwowerButton = new JButton("Reset Fields");
        borrowerManagementPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel controlsPanel = new JPanel(new GridLayout(9, 4, 20, 30));
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(nameLabel);
        controlsPanel.add(nameField);
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(ssnLabel);
        controlsPanel.add(ssnField);
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(phoneLabel);
        controlsPanel.add(phoneField);
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(addressLabel);
        controlsPanel.add(addressField);
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(resetBorwowerButton);
        controlsPanel.add(addBorwowerButton);
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        borrowerManagementPanel.add(controlsPanel, BorderLayout.CENTER);

        resetBorwowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setText("");
                ssnField.setValue("");
                addressField.setText("");
                phoneField.setValue("");
            }
        });
        addBorwowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateAddBorrower()) {
                    try {
                        String errorMessage = LibraryManagement.addBorrower(conn, nameField.getText().trim(), ssnField.getText().trim(), addressField.getText().trim(), phoneField.getText().trim());
                        if(errorMessage.trim().startsWith("Error:")) {
                            JOptionPane.showMessageDialog(controlsPanel,  errorMessage, "Add Borrower Error", JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(controlsPanel, "Added borrower with new Id: " + errorMessage, "Add Borrower Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(controlsPanel, "Error executing search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            private boolean validateAddBorrower() {

                if(nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(controlsPanel, "The name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if(ssnField.getText().replace('-',' ').trim().isEmpty() || !ssnField.isEditValid()) {
                    JOptionPane.showMessageDialog(controlsPanel, "The SSN is required and should be valid!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if(addressField.getText().trim().isEmpty() ) {
                    JOptionPane.showMessageDialog(controlsPanel, "The Address is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return true;
            }
        });

    }

}
