public class ListRowLoan extends ListRow {
    private final String id;
    private final String isbn;
    private final String title;
    private final String dateOut;
    private final String dueDate;
    private final String name;

    public ListRowLoan(String newID, String newIsbn, String newTitle, String newDateOut, String newDueDate, String newName) {
        super();
        id = newID;
        isbn = newIsbn;
        title = newTitle;
        dateOut = newDateOut;
        dueDate = newDueDate;
        name = newName;
    }

    public String getKey() {
        return id;
    }

    public void printHeader() {
        System.out.println("ID Number\tISBN\tTitle\tDate\tDue Date\tName");    }

    public void printRow() {
        System.out.println(id + "\t" + isbn + "\t" + title + "\t" + dateOut + "\t" + dueDate + "\t" + name);
    }
}
