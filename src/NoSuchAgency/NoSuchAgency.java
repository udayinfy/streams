package NoSuchAgency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.function.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;

/**
 * Copyright (C) 2016 No Such Agency
 *
 *      [ R E D A C T E D ]
 *
 */
public class NoSuchAgency {

    static final long SEED     = Long.parseLong("CS61BL", 36);
    static final Random RANDOM = new Random(SEED);
    static final long MAX_SIZE = 1000;
    static final long ROUNDS   = 1000;

    private static Map<String, List<WebTrafficRecord>> sources = new HashMap<>();
    private static List<WebTrafficRecord> dangerous = new ArrayList<>();

    /**
     * Perform the following steps in a single stream expression.
     *
     *  1. Populate the sources database by grouping by sources. Note: avoid
     *     collect since it will terminate the stream.
     *  2. For each remaining record in the stream, disregard it if there is no
     *     matching record that can be found in sources.
     *     Use WebTrafficRecord.matches.
     *  3. Remove any records that have source groups of size less than 100.
     *  4. For each remaining record, generate a fake response packet with
     *     the WebTrafficRecord.response method.
     *  5. Add all the results to dangerous.
     */
    public static void update(Stream<WebTrafficRecord> data) {
        data.peek(w -> {
            if (!sources.containsKey(w.getSource())) {
                List<WebTrafficRecord> list = new ArrayList<>();
                list.add(w);
                sources.put(w.getSource(), list);
            } else {
                sources.get(w.getSource()).add(w);
            }})
            .filter(w -> sources.get(w.getSource()).stream()
                                                   .anyMatch(x -> w.matches(x)))
            .filter(w -> sources.get(w.getSource()).size() >= 100)
            .flatMap(w -> Stream.of(w, w.response()))
            .forEach(dangerous::add);
    }

    /** Data is updated in several small batches of MAX_SIZE each. */
    public static void main(String[] args) {
        for (int i = 0; i < ROUNDS; i++) {
            update(Stream.generate(() -> WebTrafficRecord.random())
                                         .limit(MAX_SIZE));
        }
        dangerous.stream()
                 .sorted((w1,w2) -> (int) (w1.getTimestamp()
                                           - w2.getTimestamp()))
                 .forEach(System.out::println);
    }
}

