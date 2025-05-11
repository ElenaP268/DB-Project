import java.util.List;

public class ListRowFine extends ListRow {
    private final String id;
    private final String totalFines;
    protected static String columnHeaders = "ID Number\tTotal Fines";

    public ListRowFine(String newID, String newTotalFines) {
        super();
        id = newID;
        totalFines = newTotalFines;
        columnValues.addAll(List.of(id, totalFines));
    }

    public String getKey() {
        return id;
    }

    public void printHeader() {
        System.out.println(columnHeaders);    }

    public static String[] getHeaderColumnNames() {
        return columnHeaders.split("\t");
    }


    public void printRow() {
        System.out.println(id + "\t" + totalFines);
    }
}
