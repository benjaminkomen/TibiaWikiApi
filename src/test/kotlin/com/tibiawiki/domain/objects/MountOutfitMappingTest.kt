package com.tibiawiki.domain.objects

import com.tibiawiki.config.JacksonConfiguration
import com.tibiawiki.domain.enums.YesNo
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.factories.WikiObjectFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class MountOutfitMappingTest {

    private lateinit var wikiObjectFactory: WikiObjectFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        val builder = JsonMapper.builder()
        JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder)
        wikiObjectFactory = WikiObjectFactory(builder.build())
        jsonFactory = JsonFactory()
    }

    @Test
    fun mapsLiveMountWikiParametersToTypedFields() {
        val json = jsonFactory.convertInfoboxPartOfArticleToJson(LIVE_MOUNT_INFOBOX)

        val result = wikiObjectFactory.createWikiObject(json)

        assertThat(result, instanceOf(Mount::class.java))
        val mount = result as Mount
        assertThat(mount.name, `is`("Donkey"))
        assertThat(mount.actualname, `is`("donkey"))
        assertThat(mount.mountId, `is`(387))
        assertThat(mount.colourisable, `is`(YesNo.NO_LOWERCASE))
        assertThat(mount.pricecurrency, `is`("Tibia Coins"))
        assertThat(
            mount.fieldOrder().containsAll(
                listOf("actualname", "mount_id", "colourisable", "pricecurrency")
            ),
            `is`(true)
        )
    }

    @Test
    fun mapsLiveOutfitWikiParametersToTypedFields() {
        val json = jsonFactory.convertInfoboxPartOfArticleToJson(LIVE_OUTFIT_INFOBOX)

        val result = wikiObjectFactory.createWikiObject(json)

        assertThat(result, instanceOf(Outfit::class.java))
        val outfit = result as Outfit
        assertThat(outfit.name, `is`("Pirate"))
        assertThat(outfit.maleId, `is`(151))
        assertThat(outfit.femaleId, `is`(155))
        assertThat(outfit.store, `is`("no"))
        assertThat(outfit.artwork, `is`("Pirate Outfits Artwork.jpg"))
        assertThat(outfit.artwork2, `is`("Pirate Outfits Artwork 2.jpg"))
        assertThat(outfit.artwork3, `is`("Pirate Outfits Artwork 3.jpg"))
        assertThat(outfit.labels, `is`("Quest"))
        assertThat(outfit.lightcolor, `is`(94))
        assertThat(outfit.lightradius, `is`(3))
        assertThat(
            outfit.fieldOrder().containsAll(
                listOf("male_id", "female_id", "store", "artwork2", "artwork3", "labels", "lightcolor", "lightradius")
            ),
            `is`(true)
        )
    }

    @Test
    fun writesMountIdAndStoreArtworkParametersBackInFieldOrder() {
        val mountJson = wikiObjectFactory.createJSONObject(WikiObjectFixtures.mount(), "Mount")
        val outfitJson = wikiObjectFactory.createJSONObject(WikiObjectFixtures.outfit(), "Outfit")

        val mountWikitext = jsonFactory.convertJsonToInfoboxPartOfArticle(mountJson, Mount().fieldOrder())
        val outfitWikitext = jsonFactory.convertJsonToInfoboxPartOfArticle(outfitJson, Outfit().fieldOrder())

        assertThat(mountWikitext.contains("| mount_id"), `is`(true))
        assertThat(mountWikitext.contains("| colourisable"), `is`(true))
        assertThat(mountWikitext.contains("| pricecurrency"), `is`(true))
        assertThat(outfitWikitext.contains("| male_id"), `is`(true))
        assertThat(outfitWikitext.contains("| female_id"), `is`(true))
        assertThat(outfitWikitext.contains("| store"), `is`(true))
        assertThat(outfitWikitext.contains("| artwork2"), `is`(true))
        assertThat(outfitWikitext.contains("| artwork3"), `is`(true))
    }

    companion object {
        private val LIVE_MOUNT_INFOBOX = """
            {{Infobox Mount|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Donkey
            | actualname    = donkey
            | speed         = 10
            | taming_method = Use a [[Bag of Apple Slices]].
            | colourisable  = no
            | pricecurrency = Tibia Coins
            | mount_id      = 387
            | implemented   = 9.1
            }}
        """.trimIndent()

        private val LIVE_OUTFIT_INFOBOX = """
            {{Infobox Outfit|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Pirate
            | primarytype  = Quest
            | premium      = yes
            | store        = no
            | male_id      = 151
            | female_id    = 155
            | lightcolor   = 94
            | lightradius  = 3
            | implemented  = 7.8
            | artwork      = Pirate Outfits Artwork.jpg
            | artwork2     = Pirate Outfits Artwork 2.jpg
            | artwork3     = Pirate Outfits Artwork 3.jpg
            | labels       = Quest
            }}
        """.trimIndent()
    }
}
