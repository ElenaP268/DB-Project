import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class BookLoansGui {

    private Connection conn;
    private JPanel bookLoansPanel;

    public BookLoansGui(JPanel jPanel, Connection conn) {
        this.bookLoansPanel = jPanel;
        this.conn = conn;

    }

    public void show() {

    }
}
