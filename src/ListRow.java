public abstract class ListRow {
    protected Boolean isChecked;

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
}
