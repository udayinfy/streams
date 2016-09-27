package StreamDB;

import java.util.Arrays;
import java.util.List;

/**
 * A single row in this database.
 */
public class Row {

    /** Contents of this row. */
    private final String[] _data;

    /** Create a new row containing the information in the array DATA. */
    public Row(String... data) {
        _data = data;
    }

    /** Create a new row containing the information in the list COLUMNS. */
    public Row(List<String> columns) {
        this(columns.toArray(new String[columns.size()]));
    }

    /** Return all the data in this row. */
    public String[] get() {
        return _data;
    }

    /** Return the size of this row, or the number of entries. */
    public int size() {
        return _data.length;
    }

    /** Return the entry at the specific TABLE:COLUMN in this row. */
    public String get(Table table, String column) {
        return _data[table.getColumnIndex(column)];
    }

    /** Return a new row containing the entries in each TABLE:COLUMN. */
    public Row getRow(Table table, String... columns) {
        return this.getRow(table.getColumnIndices(columns));
    }

    /** Return the entry at the specific INDEX in this row. */
    private String get(int index) {
        return _data[index];
    }

    /** Return a new row containing the entries in each specified INDEX. */
    private Row getRow(int... indices) {
        String[] newData = new String[indices.length];
        for (int i = 0; i < indices.length; i++) {
            int index = indices[i];
            newData[i] = this.get(index);
        }
        return new Row(newData);
    }

    @Override
    public String toString() {
        return Arrays.toString(_data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Row ? Arrays.equals(_data, ((Row) obj)._data) : false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(_data);
    }
}

