package com.tibiawiki.domain.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PropertiesUtilTest {

    @Test
    void testGetUsername_Success() {
        String result = PropertiesUtil.getUsername();

        assertThat(result, is("Foo"));
    }

    @Test
    void testGetPassword_Success() {
        String result = PropertiesUtil.getPassword();

        assertThat(result, is("Bar"));
    }
}