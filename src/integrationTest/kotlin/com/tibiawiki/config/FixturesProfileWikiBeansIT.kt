package com.tibiawiki.config

import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.domain.repositories.FixtureArticleRepository
import com.tibiawiki.domain.repositories.JwikiArticleRepository
import com.tibiawiki.domain.wiki.WikiCallSupport
import com.tibiawiki.domain.wiki.WikiFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("fixtures")
class FixturesProfileWikiBeansIT {

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun fixturesProfileUsesDiskRepositoryAndDoesNotCreateLiveWikiClient() {
        assertThat(articleRepository, instanceOf(FixtureArticleRepository::class.java))
        assertThat(context.getBeansOfType(JwikiArticleRepository::class.java).isEmpty(), `is`(true))
        assertThat(context.getBeansOfType(WikiFactory::class.java).isEmpty(), `is`(true))
        assertThat(context.getBeansOfType(WikiCallSupport::class.java).isEmpty(), `is`(true))
        assertThat(context.getBeansOfType(WikiClientConfiguration::class.java).isEmpty(), `is`(true))
    }
}
