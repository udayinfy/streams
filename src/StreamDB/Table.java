package StreamDB;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        Predicate<Row> fsMajor = r -> r.get(t, "Major").equals("Food Systems");
        System.out.println(t.select(fsMajor, "SID", "Major"));
        System.out.println(t.select(fsMajor));

        Predicate<Row> sidFilter = r -> r.get(t, "SID").equals("105");
        System.out.println(t.select(sidFilter));
        System.out.println(t.select(sidFilter, "Firstname"));

        Predicate<Row> danielFilter = r -> r.get(t, "Firstname").equals("Daniel");
        Function<Row, String> getLastname = r -> r.get(t, "Lastname");
        System.out.println(t.export(danielFilter, getLastname));

        Predicate<Row> notMaurice = r -> !r.get(t, "Firstname").equals("Maurice");
        Function<Row, String> majorGrouper = r -> r.get(t, "Major");
        Function<Row, String> sidNameGetter = r -> r.get(t, "SID") + " " +
                                                   r.get(t, "Firstname");
        System.out.println(t.exportBy(notMaurice, majorGrouper, sidNameGetter));
    }

    /** Return a table containing new rows where PRED is true. */
    public Table select(Predicate<Row> pred) {
        return new Table(_name, _titles).addAll(select(pred, r -> r));
    }

    /** Return a table of rows consisting of COLUMNS from this table. */
    public Table select(String... columns) {
        return select(r -> true, columns);
    }

    /** Return a table containing new rows from first applying PRED and then
     *  selecting the remaining COLUMNS. */
    public Table select(Predicate<Row> pred, String... columns) {
        return new Table(_name, columns)
                   .addAll(select(pred, r -> r.getRow(this, columns)));
    }

    /** Return a stream of rows applying PRED then SELECTOR to each. */
    private Stream<Row> select(Predicate<Row> pred,
                               Function<Row, Row> selector) {
        return _rows.stream().filter(pred)
                             .map(selector);
    }

    /** Return a list of strings by applying GETTER to each row. */
    public List<String> export(Function<Row, String> getter) {
        return export(r -> true, getter);
    }

    /** Return a list of strings from rows applying PRED then GETTER. */
    public List<String> export(Predicate<Row> pred,
                               Function<Row, String> getter) {
        return _rows.stream().filter(pred)
                             .map(getter)
                             .collect(toList());
    }

    /** Return a map of strings to results grouped according to GROUPER and with
     *  values selected by GETTER. */
    public Map<String, List<String>> exportBy(Function<Row, String> grouper,
                                              Function<Row, String> getter) {
        return exportBy(r -> true, grouper, getter);
    }

    /** Return a map of strings to results where the row satisfies PRED, grouped
     *  according to GROUPER, and values selected by GETTER. */
    public Map<String, List<String>> exportBy(Predicate<Row> pred,
                                              Function<Row, String> grouper,
                                              Function<Row, String> getter) {
        return _rows.stream().filter(pred)
                             .collect(groupingBy(grouper,
                                      mapping(getter, toList())));
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
    int getColumnIndex(String column) {
        return _titlesIndex.containsKey(column) ? _titlesIndex.get(column) : -1;
    }

    /** Return the column indices associated with each title in COLUMNS. */
    int[] getColumnIndices(String... columns) {
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
}

