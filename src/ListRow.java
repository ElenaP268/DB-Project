import java.util.ArrayList;
import java.util.List;

public abstract class ListRow {
    protected Boolean isChecked;
    protected List<String> columnValues = new ArrayList<>();

    protected static String columnHeaders = "";

    public ListRow () {
        isChecked = false;
    }

    public void check() {
        isChecked = !isChecked;
    }

    public void printHeader() {
    }

    public void printRow() {
    }

    public String getKey() {
        return null;
    }

    public boolean checked() {
        return isChecked;
    }

    public String[] getColumnsValues () {
        return columnValues.toArray(new String[0]);
    }
}
