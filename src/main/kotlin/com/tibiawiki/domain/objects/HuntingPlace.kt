package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.tibiawiki.domain.enums.City
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Star

/**
 * Infobox Hunt model.
 *
 * Recommended level (`lvl*`), skill (`sk*`), and defence (`def*`) columns follow the wiki
 * template: knight, paladin, and mage only. Monk columns are omitted until TibiaWiki adds them.
 */
data class HuntingPlace(
    override val name: String? = null,
    override val implemented: String? = null,
    val image: String? = null,
    val city: City? = null,
    val location: String? = null,
    val vocation: String? = null,
    val lvlknights: String? = null,
    val lvlpaladins: String? = null,
    val lvlmages: String? = null,
    val skknights: String? = null,
    val skpaladins: String? = null,
    val skmages: String? = null,
    val defknights: String? = null,
    val defpaladins: String? = null,
    val defmages: String? = null,
    @JsonManagedReference
    val lowerlevels: List<HuntingPlaceSkills>? = null,
    val loot: String? = null,
    val lootstar: Star? = null,
    val exp: String? = null,
    val expstar: Star? = null,
    val bestloot: String? = null,
    val bestloot2: String? = null,
    val bestloot3: String? = null,
    val bestloot4: String? = null,
    val bestloot5: String? = null,
    val map: String? = null,
    val map2: String? = null,
    val map3: String? = null,
    val map4: String? = null
) : WikiObject(
    name = name,
    implemented = implemented
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.HUNT.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "image", "implemented", "city", "location", "vocation", "lvlknights", "lvlpaladins",
            "lvlmages", "skknights", "skpaladins", "skmages", "defknights", "defpaladins", "defmages", "lowerlevels",
            "loot", "lootstar", "exp", "expstar", "bestloot", "bestloot2", "bestloot3", "bestloot4", "bestloot5",
            "map", "map2", "map3", "map4"
        )
    }
}
