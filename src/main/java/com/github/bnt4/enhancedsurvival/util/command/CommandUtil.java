package com.github.bnt4.enhancedsurvival.util.command;

import java.util.Collection;
import java.util.Iterator;

public class CommandUtil {

    public static void addMatches(Collection<String> matches, String search, String... options) {
        for (String s : options) {
            if (s.toLowerCase().startsWith(search.toLowerCase())) {
                matches.add(s);
            }
        }
    }

    public static void addMatches(Collection<String> matches, String search, Iterable<String> options) {
        for (String s : options) {
            if (s.toLowerCase().startsWith(search.toLowerCase())) {
                matches.add(s);
            }
        }
    }

}
