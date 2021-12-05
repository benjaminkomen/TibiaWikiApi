package com.tibiawiki.domain.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.tibiawiki.domain.utils.ListUtilKt.concatenate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;


class ListUtilTest {

    @Test
    void testConcatenate() {
        assertThat(concatenate(new ArrayList<>()), hasSize(0));
        assertThat(concatenate(Collections.singletonList("foo")), hasSize(1));
        assertThat(concatenate(Collections.singletonList("foo"), Collections.singletonList("bar")), hasSize(2));
        assertThat(concatenate(Arrays.asList(1, 2, 3), Collections.singletonList("bar")), hasSize(4)); // TODO mixing of generic types should actually not be possible..
    }

}