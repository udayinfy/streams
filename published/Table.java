import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;

/**
 * A self-contained table program for StreamDB.
 */
public class Table {

    /** The name of this database table. */
    private final String _name;
    /** All the titles for the rows in this table. */
    private final String[] _titles;
    /** A mapping of all the titles to their indices for fast lookup. */
    private final Map<String, Integer> _titlesIndex;
    /** All the rows in this table. */
    private final Set<Row> _rows;

    public static void main(String[] args) {
        Table t = new Table("Students", "SID", "Lastname", "Firstname", "Major");
        t.add("101", "Yao", "Alan", "Food Systems")
         .add("102", "Chen", "Antares", "Literature")
         .add("103", "Nguyen", "Daniel", "Parrot Biology")
         .add("104", "Lee", "Maurice", "Chemical Engineering")
         .add("105", "Jian", "Lisa", "Cosmology")
         .add("106", "Kim", "Sarah", "Parrot Biology");
        System.out.println(t);

        Predicate<Row> fsMajor = r -> r.get("Major").equals("Food Systems");
        System.out.println(t.select(fsMajor, "SID", "Major"));
        System.out.println(t.select(fsMajor));

        Predicate<Row> sidFilter = r -> r.get("SID").equals("105");
        System.out.println(t.select(sidFilter));
        System.out.println(t.select(sidFilter, "Firstname"));

        Predicate<Row> danielFilter = r -> r.get("Firstname").equals("Daniel");
        Function<Row, String> getLastname = r -> r.get("Lastname");
        System.out.println(t.export(danielFilter, getLastname));

        Predicate<Row> notMaurice = r -> !r.get("Firstname").equals("Maurice");
        Function<Row, String> majorGrouper = r -> r.get("Major");
        Function<Row, String> sidNameGetter = r -> r.get("SID") + " " +
                                                   r.get("Firstname");
        System.out.println(t.exportBy(notMaurice, majorGrouper, sidNameGetter));
    }

    /** Return a table containing new rows where PRED is true. */
    public Table select(Predicate<Row> pred) {
        // FIXME
        return null;
    }

    /** Return a table of rows consisting of COLUMNS from this table. */
    public Table select(String... columns) {
        // FIXME
        return null;
    }

    /** Return a table containing new rows from first applying PRED and then
     *  selecting the remaining COLUMNS. */
    public Table select(Predicate<Row> pred, String... columns) {
        // FIXME
        return null;
    }

    /** Return a stream of rows applying PRED then SELECTOR to each. */
    private Stream<Row> select(Predicate<Row> pred, Function<Row, Row> selector) {
        // FIXME
        return null;
    }

    /** Return a list of strings by applying GETTER to each row. */
    public List<String> export(Function<Row, String> getter) {
        // FIXME
        return null;
    }

    /** Return a list of strings from rows applying PRED then GETTER. */
    public List<String> export(Predicate<Row> pred, Function<Row, String> getter) {
        // FIXME
        return null;
    }

    /** Return a map of strings to results grouped according to GROUPER and with
     *  values selected by GETTER. */
    public Map<String, List<String>> exportBy(Function<Row, String> grouper,
                                              Function<Row, String> getter) {
        // FIXME
        return null;
    }

    /** Return a map of strings to results where the row satisfies PRED, grouped
     *  according to GROUPER, and values selected by GETTER. */
    public Map<String, List<String>> exportBy(Predicate<Row> pred,
                                              Function<Row, String> grouper,
                                              Function<Row, String> getter) {
        // FIXME
        return null;
    }

    /** Create a new table with NAME and TITLES. */
    public Table(String name, String... titles) {
        if (Arrays.stream(titles).distinct().count() != titles.length) {
            throw new IllegalArgumentException("column titles are not unique");
        }
        _name = name;
        _titles = titles;
        _titlesIndex = new HashMap<>(titles.length);
        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            _titlesIndex.put(title, i);
        }
        _rows = new HashSet<>();
    }

    /** Add a ROW to this table and return the TABLE. Chainable. */
    public Table add(Row row) {
        if (row.size() != _titles.length) {
            throw new IllegalArgumentException("rows do not match with titles");
        }
        _rows.add(row);
        return this;
    }

    /** Add a new row of DATA to this table and return the TABLE. Chainable. */
    public Table add(String... data) {
        return add(new Row(data));
    }

    /** Add multiple ROWS to this table and return the TABLE. Chainable. */
    public Table addAll(Stream<Row> rows) {
        rows.forEach(this::add);
        return this;
    }

    /** Return the name of this table. */
    public String name() {
        return _name;
    }

    /** Return the title at the specified COLUMN in this table. */
    public String title(int column) {
        return _titles[column];
    }

    /** Return the size, or number of rows, in this table. */
    public int size() {
        return _rows.size();
    }

    /** Return the column index associated with a single COLUMN or -1 if the
     *  column does not exist. */
    private int getColumnIndex(String column) {
        return _titlesIndex.containsKey(column) ? _titlesIndex.get(column) : -1;
    }

    /** Return the column indices associated with each title in COLUMNS. */
    private int[] getColumnIndices(String... columns) {
        int[] result = new int[columns.length];
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            int index = getColumnIndex(column);
            if (index == -1) {
                throw new IllegalArgumentException("no such column " + column);
            } else {
                result[i] = index;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(_name).append('\n');
        result.append(Arrays.toString(_titles));
        _rows.stream().forEach(r -> result.append('\n').append(r.toString()));
        return result.toString();
    }

    /** A single row in this database. */
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

        /** Return the entry at the specific COLUMN in this row. */
        public String get(String column) {
            return _data[getColumnIndex(column)];
        }

        /** Return a new row containing the entries in each specified COLUMN. */
        public Row getRow(String... columns) {
            return this.getRow(getColumnIndices(columns));
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
            return obj instanceof Row ? Arrays.equals(_data, ((Row) obj)._data)
                                      : false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(_data);
        }
    }
}

