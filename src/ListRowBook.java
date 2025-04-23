public class ListRowBook extends ListRow {
    private final String isbn;
    private final String title;
    private final String author;
    private final String status;
    private final String borrowerID;

    public ListRowBook(String newIsbn, String newTitle, String newAuthor, String newStatus, String newBorrowerID) {
        super();
        isbn = newIsbn;
        title = newTitle;
        author = newAuthor;
        status = newStatus;
        borrowerID = newBorrowerID;
    }

    public void printHeader() {
        System.out.println("ISBN\tTITLE\tAUTHORS\tSTATUS");
    }

    public String getIsbn() {
        return isbn;
    }

    public void printRow() {
        System.out.println(isbn + "\t" + title + "\t" + author + "\t" + status + "\t" + borrowerID);
    }
}
