package com.tibiawiki.domain.utils

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class PropertiesUtilTest {

    @Test
    fun testGetUsername_Success() {
        assertThat(PropertiesUtil.getUsername(), `is`("Foo"))
    }

    @Test
    fun testGetPassword_Success() {
        assertThat(PropertiesUtil.getPassword(), `is`("Bar"))
    }
}
