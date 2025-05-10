import java.util.Arrays;
import java.util.List;

public class ListRowBook extends ListRow {
    private final String isbn;
    private final String title;
    private final String author;
    private final String status;
    private final String borrowerID;

    protected static String columnHeaders = "ISBN\tTITLE\tAUTHORS\tSTATUS\tBORROWER ID";

    public ListRowBook(String newIsbn, String newTitle, String newAuthor, String newStatus, String newBorrowerID) {
        super();
        isbn = newIsbn;
        title = newTitle;
        author = newAuthor;
        status = newStatus;
        borrowerID = newBorrowerID;
        columnValues.addAll(List.of(isbn, title, author, status, (borrowerID == null ? "null": borrowerID) ));
    }

    public void printHeader() {
        System.out.println("ISBN\tTITLE\tAUTHORS\tSTATUS");
    }

    public String getHeader() {
        return columnHeaders;
    }

    public static String[] getHeaderColumnNames() {
        return columnHeaders.split("\t");
    }

    public String getKey() {
        return isbn;
    }

    public void printRow() {
        System.out.println(isbn + "\t" + title + "\t" + author + "\t" + status + "\t" + borrowerID);
    }

    @Override
    public String toString() {
        return isbn + "\t" + title + "\t" + author + "\t" + status + "\t" + borrowerID;
    }


}
