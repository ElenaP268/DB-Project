import javax.swing.*;
import java.sql.Connection;

public class FinesGUI {
    private Connection conn;
    private JPanel finesGui;

    public FinesGUI(JPanel jPanel, Connection conn) {
        this.finesGui = jPanel;
        this.conn = conn;
    }

    public void show() {

    }

}
