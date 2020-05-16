package com.tibiawiki;

import org.springframework.http.HttpHeaders;

public class TestUtils {

    public static HttpHeaders makeHttpHeaders(String editSummary) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-WIKI-Edit-Summary", editSummary);
        return httpHeaders;
    }
}
