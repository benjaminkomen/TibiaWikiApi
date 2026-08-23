package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonBackReference

data class HuntingPlaceSkills(
    val areaname: String? = null,
    val lvlknights: String? = null,
    val lvlpaladins: String? = null,
    val lvlmages: String? = null,
    val skknights: String? = null,
    val skpaladins: String? = null,
    val skmages: String? = null,
    val defknights: String? = null,
    val defpaladins: String? = null,
    val defmages: String? = null,
    @JsonBackReference
    val huntingPlace: HuntingPlace? = null
) {
    companion object {
        fun fieldOrder(): List<String> {
            return listOf(
                "areaname",
                "lvlknights",
                "lvlpaladins",
                "lvlmages",
                "skknights",
                "skpaladins",
                "skmages",
                "defknights",
                "defpaladins",
                "defmages"
            )
        }
    }
}
