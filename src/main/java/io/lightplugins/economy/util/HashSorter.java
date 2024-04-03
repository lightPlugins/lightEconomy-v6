package io.lightplugins.economy.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HashSorter {

    private HashMap<String, Double> list;

    public HashSorter(HashMap<String, Double> list) {
        this.list = list;
    }

    public TreeMap<String, Double> getSortedMap() {
        // Creating a comparator object to compare the values
        ValueComparator valueComparator = new ValueComparator(this.list);

        // Creating a TreeMap with the comparator for sorting
        TreeMap<String, Double> sortedMap = new TreeMap<>(valueComparator);

        // Adding all entries from the original HashMap to the sorted TreeMap
        sortedMap.putAll(this.list);

        return sortedMap;
    }

    // Inner class to compare and sort values (should not be static)
    @SuppressWarnings("InnerClassMayBeStatic")
    class ValueComparator implements Comparator<String> {
        Map<String, Double> base;

        public ValueComparator(HashMap<String, Double> map) {
            this.base = map;
        }

        @Override
        public int compare(String a, String b) {
            // Comparing the values and returning the result
            // Sorting in descending order here
            if (base.get(a).equals(base.get(b))) {
                return 0; // Returning 0 if the values are equal
            } else if (base.get(a) >= base.get(b)) {
                return -1; // Returning -1 if a is greater than b
            } else {
                return 1; // Returning 1 if b is greater than a
            }
        }
    }
}