public class ListRowFine extends ListRow {
    private final String id;
    private final String totalFines;

    public ListRowFine(String newID, String newTotalFines) {
        super();
        id = newID;
        totalFines = newTotalFines;
    }

    public void printHeader() {
        System.out.println("ID Number\tTotal Fines");    }

    public void printRow() {
        System.out.println(id + "\t" + totalFines);
    }
}