package com.tibiawiki

import org.fastily.jwiki.core.Wiki
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TibiaWikiApiApplicationTest {

    @MockBean
    private lateinit var wiki: Wiki

    @Test
    fun startupTest() {
        // this is all we need
    }
}
