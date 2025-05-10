import javax.swing.*;
import java.sql.Connection;

public class BorrowerManagementGUI  {

    private Connection conn;
    private JPanel borrowerManagementPanel;

    public BorrowerManagementGUI(JPanel jPanel, Connection conn) {
        this.borrowerManagementPanel = jPanel;
        this.conn = conn;
    }

    public void show() {

    }
}
