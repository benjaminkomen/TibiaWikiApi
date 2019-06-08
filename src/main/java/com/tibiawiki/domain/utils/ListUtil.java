package com.tibiawiki.domain.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtil {

    private ListUtil() {
        // don't instantiate this class, it has only static members
    }

    @SafeVarargs
    public static <E> List<E> concatenate(List<E>... collections) {

        if (collections == null || collections.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
