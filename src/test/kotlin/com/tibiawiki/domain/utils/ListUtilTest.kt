package com.tibiawiki.domain.utils

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

class ListUtilTest {

    @Test
    fun testConcatenate() {
        assertThat(concatenate(emptyList<Any>()), hasSize(0))
        assertThat(concatenate(listOf("foo")), hasSize(1))
        assertThat(concatenate(listOf("foo"), listOf("bar")), hasSize(2))
        // mixing of generic types should actually not be possible..
        assertThat(concatenate<Any>(listOf(1, 2, 3), listOf("bar")), hasSize(4))
    }
}
