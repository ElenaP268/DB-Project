import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class LibraryManagement {
    static final String DB_URL = "jdbc:mysql://localhost:3306/Library";
    static final String USERNAME = "root"; // Or username
    static final String PASSWORD = "password"; // Replace with actual password

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        ArrayList<ListRow> searchResult;

        // GUI goes here
//        searchResult = search(conn, "Williamson");
//        printTable(searchResult);
//
//        searchResult = displayFines(conn);
//        printTable(searchResult);
//
//        searchResult = loanSearch(conn, "ID001001");
//        printTable(searchResult);
    }

    public static void printTable (ArrayList<ListRow> searchResult) {
        if (searchResult.isEmpty()) {
            System.out.println("No records found.");
        }
        else {
            searchResult.get(0).printHeader();
            for (ListRow row : searchResult) {
                row.printRow();
            }
        }
    }

    public static ArrayList<ListRow> search(Connection conn, String searchTerm) throws SQLException {
        searchTerm = "%" + searchTerm + "%";
        String query = "SELECT B.Isbn AS ISBN, B.Title AS TITLE, A.Name AS AUTHORS, "+
                        "CASE WHEN BL.Date_in IS NULL AND BL.Date_out IS NOT NULL THEN 'Checked Out' ELSE 'Available' END AS STATUS, "+
                        "BL.Card_id AS BORROWER "+
                        "FROM BOOK AS B LEFT JOIN BOOK_AUTHORS AS BA ON B.Isbn = BA.Isbn "+
                        "LEFT JOIN AUTHORS AS A ON BA.Author_id = A.Author_id "+
                        "LEFT JOIN BOOK_LOANS AS BL ON B.Isbn = BL.Isbn "+
                        "WHERE B.Isbn LIKE ? OR B.Title LIKE ? OR A.Name LIKE ? " + 
                        "ORDER BY B.Isbn;";

        PreparedStatement st = conn.prepareStatement(query);

        st.setString(1, searchTerm);
        st.setString(2, searchTerm);
        st.setString(3, searchTerm);

        ResultSet rs = st.executeQuery();

        ArrayList<ListRow> searchResult = new ArrayList<>();
        String prevIsbn = null;

        while (rs.next()) {
            String isbn = rs.getString("ISBN");
            if (!isbn.equals(prevIsbn)) {
                query = "SELECT A.Name FROM BOOK AS B LEFT JOIN BOOK_AUTHORS AS BA ON B.Isbn = BA.Isbn LEFT JOIN AUTHORS AS A ON BA.Author_id = A.Author_id  WHERE B.ISBN = ? ;";
                PreparedStatement st2 = conn.prepareStatement(query);
                st2.setString(1, isbn );
                ResultSet rs2 = st2.executeQuery();

                rs2.next();
                StringBuilder authors = new StringBuilder(rs2.getString("Name"));
                while (rs2.next()) {
                    authors.append(", ").append(rs2.getString("Name"));
                }

                searchResult.add(new ListRowBook(isbn, rs.getString("TITLE"), authors.toString(), rs.getString("STATUS"), rs.getString("BORROWER")));
                prevIsbn = isbn;
            }
        }

        return searchResult;
    }

    public static void addBorrower(Connection conn, String name, String ssn, String address) throws SQLException {
        addBorrower(conn, name, ssn, address, null);
    }

    public static String addBorrower(Connection conn, String name, String ssn, String address, String phone) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Borrower WHERE Ssn = '" + ssn + "'");
        if (rs.next()) { // Non-empty result
            printError("Borrower already has a library card.");
            return "Error: Borrower already has a library card.";
        }

        String id = generateBorrowerID(conn);

        PreparedStatement pst = conn.prepareStatement("INSERT INTO Borrower (Card_id, Ssn, Bname, Address, Phone) VALUES (?, ?, ?, ?, ?);");
        pst.setString(1, id);
        pst.setString(2, ssn);
        pst.setString(3, name);
        pst.setString(4, address);
        pst.setString(5, phone);

        pst.executeUpdate();
        return id;
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

    public static ArrayList<ListRow> loanSearch(Connection conn, String searchTerm) throws SQLException {
        String query = "SELECT bl.Loan_id, bl.Isbn, bk.Title, bl.Date_out, bl.Due_date, b.Bname " +
                        "FROM BOOK_LOANS bl " +
                        "JOIN BOOK bk ON bl.Isbn = bk.Isbn " +
                        "JOIN BORROWER b ON bl.Card_id = b.Card_id " +
                        "WHERE (bl.Isbn = ? OR b.Card_id = ? OR b.Bname LIKE ?) " +
                        "AND bl.Date_in IS NULL;";

        PreparedStatement st = conn.prepareStatement(query);

        st.setString(1, searchTerm);
        st.setString(2, searchTerm);
        st.setString(3, "%" + searchTerm + "%");

        ResultSet rs = st.executeQuery();

        ArrayList<ListRow> searchResult = new ArrayList<>();

        while (rs.next()) {
            searchResult.add(new ListRowLoan(rs.getString(1), rs.getString(2),rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
        }

        return searchResult;
    }

    public static boolean checkoutFromIsbn(Connection conn, String isbn, String borrowerID, Component parentComponent) throws SQLException {
        if(borrowerID != null && !borrowerID.trim().isEmpty()) {
            String errorMessage = checkout(conn, isbn, borrowerID);
            if(!errorMessage.trim().isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent,  errorMessage, "Checkout Error", JOptionPane.WARNING_MESSAGE);
                return false;
            } else {
                JOptionPane.showMessageDialog(parentComponent,  "Successfully checked out book", "Checkout Success", JOptionPane.PLAIN_MESSAGE);
                return true;
            }
        }
        return false;
    }

    public static String checkout(Connection conn, String isbn, String borrowerID) throws SQLException {
        // checkout and add info to book_loans
        String errorMessage = "";
        if( !isValidBorrower(conn, borrowerID)) {
            errorMessage = "Borrower with card id " + borrowerID + " do not exist.";
            printError(errorMessage);
            return errorMessage;
        }
        if (isCheckedOut(conn, isbn)) {
            errorMessage = "Book with ISBN " + isbn + " is already checked out.";
            printError(errorMessage);
            return errorMessage;
        }
        if (borrowerLimitReached(conn, borrowerID)) {
            errorMessage = "Borrower already has three books checked out.";
            printError(errorMessage);
            return errorMessage;
        }
        if (borrowerHasFines(conn, borrowerID)) {
            errorMessage = "Borrower has unpaid fines, cannot check out more books.";
            printError(errorMessage);
            return errorMessage;
        }

        String query = "INSERT INTO BOOK_LOANS (Loan_id, Card_id, Isbn, Date_out, Due_date) " +
                "SELECT " +
                "?," +
                "    b.Card_id, " +
                "    bk.Isbn, " +
                "    CURRENT_DATE, " +
                "    DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY) " +
                "FROM BORROWER b, BOOK bk " +
                "WHERE b.Card_id = ? " +
                "AND bk.Isbn = ?;";
        PreparedStatement st = conn.prepareStatement(query);
        st.setInt(1, generateLoanID(conn));
        st.setString(2, borrowerID);
        st.setString(3, isbn);
        int count = st.executeUpdate();
        if(count <= 0) {
            errorMessage = "Could not checkout book. Unknown Error.";
        }
        return errorMessage;
    }

    public static String getBorrowerID(Component parentComponent) {
        String borrowerID = "";
        //prompt user for borrower ID
        while(borrowerID.equalsIgnoreCase("")) {
            borrowerID = JOptionPane.showInputDialog(parentComponent, "Enter your Card ID:", "Card Input Dialog", JOptionPane.QUESTION_MESSAGE);
            if (borrowerID == null) break;
        }

        return borrowerID;
    }

    public static int generateLoanID (Connection conn) throws SQLException {
        int largestID = 0;
        String query = "SELECT Loan_id FROM Book_Loans ORDER BY Loan_id DESC LIMIT 1;";

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        if(rs.next()){
            largestID = 1 + rs.getInt(1);
        }

        return largestID;
    }

    public static boolean isCheckedOut(Connection conn, String isbn) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT 1 FROM BOOK_LOANS bl, BOOK bk WHERE bl.Isbn = bk.Isbn AND bl.Date_in IS NULL AND bk.isbn = '" + isbn + "';");

        return rs.next(); // True if the books is already in the loan list
    }

    public static boolean borrowerLimitReached(Connection conn, String borrowerID) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM BOOK_LOANS WHERE Card_id = '" + borrowerID + "' AND Date_in IS NULL");

        rs.next();
        return rs.getInt(1) >= 3; // True if borrower has reached borrow limit
    }
    public static boolean isValidBorrower(Connection conn, String borrowerID) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM BORROWER WHERE Card_id = '" + borrowerID + "'");
        rs.next();
        return rs.getInt(1) == 1; // True if borrower has reached borrow limit
    }

    public static boolean borrowerHasFines(Connection conn, String borrowerID) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM FINES f WHERE f.Loan_id IN (SELECT Loan_id FROM BOOK_LOANS WHERE Card_id = '" + borrowerID + "') AND f.Paid = 0;");

        return rs.next(); // True if borrower has unpaid fines
    }

    public static void checkInFromSearch(Connection conn, ArrayList<ListRow> searchResult) throws SQLException {
        for (ListRow row : searchResult) {
            if (row.checked())
                checkIn(conn, row.getKey());
        }
    }

    public static void checkIn(Connection conn, String loanID) throws SQLException {
        String query = "UPDATE BOOK_LOANS SET Date_in = CURRENT_DATE WHERE Loan_id = " + loanID + " AND Date_in IS NULL;";
        Statement st = conn.createStatement();
        st.executeUpdate(query);
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

    public static ArrayList<ListRow> displayFines(Connection conn) throws SQLException {
        String query = "SELECT Card_id, SUM(Fine_amt) FROM Book_Loans NATURAL JOIN Fines WHERE Paid IS FALSE GROUP BY Card_id;";

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        ArrayList<ListRow> searchResult = new ArrayList<>();
        while (rs.next()) {
            searchResult.add(new ListRowFine(rs.getString(1), rs.getString(2)));
        }
        return searchResult;
    }

    public static void payFinesFromSearch(Connection conn, ArrayList<ListRow> searchResult) throws SQLException {
        for (ListRow row : searchResult) {
            if (row.checked())
                payFines(conn, row.getKey());
        }
    }

    public static void payFines(Connection conn, String cardID) throws SQLException {
        String query = "SELECT Loan_id FROM Book_loans WHERE Card_id = ? AND Date_in IS NULL;";

        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, cardID);

        ResultSet rs = st.executeQuery();

        if(rs.next()){
            printError("Cannot pay fines for unreturned books.");
            return;
        }

        query = "UPDATE Fines SET Paid = TRUE WHERE Loan_id IN(SELECT Loan_id FROM Book_Loans WHERE Card_id = ?);";
        PreparedStatement st2 = conn.prepareStatement(query);

        st2.setString(1, cardID);

        st2.executeUpdate();
    }

    public static void printError(String error) {
        System.out.println(error);
    }
}
