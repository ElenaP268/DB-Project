import java.math.BigDecimal;
import java.sql.*;

public class LibraryManagement {
    static final String DB_URL = "jdbc:mysql://localhost:3306/Library";
    static final String USERNAME = "root"; // Or username
    static final String PASSWORD = "password"; // Replace with actual password

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

        // GUI goes here
        search(conn, "Williamson");
    }

    public static void search(Connection conn, String searchTerm) throws SQLException {
        searchTerm = "%" + searchTerm + "%";
        String query = "SELECT B.Isbn AS ISBN, B.Title AS TITLE, A.Name AS AUTHORS, "+
                        "CASE WHEN BL.Date_in IS NULL THEN 'Checked Out' ELSE 'Available' END AS STATUS "+
                        "FROM BOOK AS B LEFT JOIN BOOK_AUTHORS AS BA ON B.Isbn = BA.Isbn "+
                        "LEFT JOIN AUTHORS AS A ON BA.Author_id = A.Author_id "+
                        "LEFT JOIN BOOK_LOANS AS BL ON B.Isbn = BL.Isbn "+
                        "WHERE B.Isbn LIKE ? OR B.Title LIKE ? OR A.Name LIKE ?;";

        PreparedStatement st = conn.prepareStatement(query);

        st.setString(1, searchTerm);
        st.setString(2, searchTerm);
        st.setString(3, searchTerm);

        ResultSet rs = st.executeQuery();

        // REPLACE print statements with GUI integration
        System.out.println("ISBN\tTITLE\tAUTHORS\tSTATUS");
        while (rs.next()) {
            String isbn = rs.getString("ISBN");
            String title = rs.getString("TITLE");
            String authors = rs.getString("AUTHORS");
            String status = rs.getString("STATUS");

            System.out.println(isbn + "\t" + title + "\t" + authors + "\t" + status);
        }
    }

    public static void addBorrower(Connection conn, String name, String ssn, String address) throws SQLException {
        addBorrower(conn, name, ssn, address, null);
    }

    public static void addBorrower(Connection conn, String name, String ssn, String address, String phone) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Borrower WHERE Ssn = '" + ssn + "'");
        if (rs.next()) { // Non-empty result
            System.out.println("Borrower already has a library card.");
            return;
        }

        String id = generateBorrowerID(conn);

        PreparedStatement pst = conn.prepareStatement("INSERT INTO Borrower (Card_id, Ssn, Bname, Address, Phone) VALUES (?, ?, ?, ?, ?);");
        pst.setString(1, id);
        pst.setString(2, ssn);
        pst.setString(3, name);
        pst.setString(4, address);
        pst.setString(5, phone);

        pst.executeUpdate();
    }

    public static String generateBorrowerID(Connection conn) throws SQLException {
        String newID = "ID";
        int largestID = 0;
        String query = "SELECT Card_id FROM Borrower ORDER BY Card_ID DESC LIMIT 1;";

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        if(rs.next()){
            largestID = Integer.parseInt(rs.getString(1).substring(2));
        }
        largestID += 1;

        for (int i = (int)(Math.log10(largestID)+1); i < 6; i++) { //Add leading 0s
            newID += "0";
        }
        newID += largestID;

        return newID;
    }

    public static void updateFines(Connection conn) throws SQLException {
        String query = "SELECT  DATEDIFF(CURRENT_DATE(), Due_date) AS interval_to_today, DATEDIFF(Date_in, Due_date) AS interval_to_date_in, B.Loan_id, Fine_amt, Paid FROM Book_Loans B LEFT JOIN Fines F ON B.Loan_id = F.Loan_id;";

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            BigDecimal newFineAmt;
            BigDecimal intervalToToday = rs.getBigDecimal(1);
            BigDecimal intervalToDateIn = rs.getBigDecimal(2);

            if (rs.wasNull()) // Detects default value, i.e. no Date_in
                newFineAmt = intervalToToday.multiply(new BigDecimal("0.25"));
            else
                newFineAmt = intervalToDateIn.multiply(new BigDecimal("0.25"));

            int loanID = rs.getInt(3);
            BigDecimal fineAmt = rs.getBigDecimal(4);
            boolean paid = rs.getBoolean(5);

            if (rs.wasNull()) { // Needs new entry in Fines
                String query2 = "INSERT INTO Fines (Loan_id, Fine_amt, Paid) VALUES (?, ?, 0);";
                PreparedStatement st2 = conn.prepareStatement(query2);

                System.out.print(loanID + ", " + newFineAmt);
                st2.setInt(1, loanID);
                st2.setBigDecimal(2, newFineAmt);

                st2.executeUpdate();

                System.out.println("Added loan " + loanID + " as " + newFineAmt);
            }
            else if (!paid && !fineAmt.equals(newFineAmt)) { // Needs updated entry in Fines
                String query2 = "UPDATE Fines SET Fine_amt = ? WHERE Loan_id = ?;";
                PreparedStatement st2 = conn.prepareStatement(query2);

                st2.setBigDecimal(1, newFineAmt);
                st2.setInt(2, loanID);

                System.out.println(loanID + ", " + newFineAmt);
                st2.executeUpdate();

                System.out.println("Set loan " + loanID + " to " + newFineAmt);
            }
        }
    }

    public static void displayFines(Connection conn) throws SQLException {
        String query = "SELECT Card_id, SUM(Fine_amt) FROM Book_Loans NATURAL JOIN Fines WHERE Paid IS FALSE GROUP BY Card_id;";

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        System.out.println("ID Number\tTotal Fines");
        while (rs.next()){
            System.out.println(rs.getString(1) + "\t$" + rs.getString(2));
        }
    }

    public static void payFines(Connection conn, String cardID) throws SQLException {
        String query = "SELECT Loan_id FROM Book_loans WHERE Card_id = ? AND Date_in IS NULL;";

        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, cardID);

        ResultSet rs = st.executeQuery();

        if(rs.next()){
            System.out.println("Cannot pay fines for unreturned books.");
            return;
        }

        query = "UPDATE Fines SET Paid = TRUE WHERE Loan_id IN(SELECT Loan_id FROM Book_Loans WHERE Card_id = ?);";
        PreparedStatement st2 = conn.prepareStatement(query);

        st2.setString(1, cardID);

        st2.executeUpdate();
    }
}
