package com.tibiawiki

import org.springframework.http.HttpHeaders

object TestUtils {

    fun makeHttpHeaders(editSummary: String): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders["X-WIKI-Edit-Summary"] = editSummary
        return httpHeaders
    }
}
