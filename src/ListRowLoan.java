import java.util.Arrays;
import java.util.List;

public class ListRowLoan extends ListRow {
    private final String id;
    private final String isbn;
    private final String title;
    private final String dateOut;
    private final String dueDate;
    private final String name;

    protected static String columnHeaders = "ID Number\tISBN\tTitle\tDate\tDue Date\tName";

    public ListRowLoan(String newID, String newIsbn, String newTitle, String newDateOut, String newDueDate, String newName) {
        super();
        id = newID;
        isbn = newIsbn;
        title = newTitle;
        dateOut = newDateOut;
        dueDate = newDueDate;
        name = newName;
        columnValues.addAll(List.of(id, isbn, title, dateOut, dueDate, name));
    }

    public String getKey() {
        return id;
    }
    public String getHeader() {
        return columnHeaders;
    }
    public static String[] getHeaderColumnNames() {
        return columnHeaders.split("\t");
    }
    public void printHeader() {
        System.out.println("ID Number\tISBN\tTitle\tDate\tDue Date\tName");    }

    public void printRow() {
        System.out.println(id + "\t" + isbn + "\t" + title + "\t" + dateOut + "\t" + dueDate + "\t" + name);
    }
}
