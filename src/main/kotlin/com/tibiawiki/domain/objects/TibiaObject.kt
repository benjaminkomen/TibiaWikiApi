package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonGetter
import com.tibiawiki.domain.enums.*
import java.math.BigDecimal

data class TibiaObject(
    val itemid: List<Int>? = emptyList(),
    @Deprecated("replaced by objectclass")
    val primarytype: String? = null,
    @Deprecated(message = "never used")
    val sprites: String? = null,
    val objectclass: String,
    val secondarytype: String? = null,
    val tertiarytype: String? = null,
    val flavortext: String? = null,
    val words: String? = null,
    val sounds: List<String>? = emptyList(),
    val lightradius: Int? = null,
    val lightcolor: Int? = null,
    val volume: Int? = null,
    val destructable: YesNo? = null,
    val immobile: YesNo? = null,
    val walkable: YesNo? = null,
    val walkingspeed: Int? = null,
    val unshootable: YesNo? = null,
    val blockspath: YesNo? = null,
    val pickupable: YesNo? = null,
    val holdsliquid: YesNo? = null,
    val usable: YesNo? = null,
    val writable: YesNo? = null,
    val rewritable: YesNo? = null,
    val writechars: Int? = null,
    val levelrequired: Int? = null,
    val vocrequired: String? = null,
    val mlrequired: Int? = null,
    val hands: Hands? = null,
    val weapontype: WeaponType? = null,
    val attack: String? = null,
    @get:JsonGetter("fire_attack") val fireAttack: Int? = null,
    @get:JsonGetter("earth_attack") val earthAttack: Int? = null,
    @get:JsonGetter("ice_attack") val iceAttack: Int? = null,
    @get:JsonGetter("energy_attack") val energyAttack: Int? = null,
    @get:JsonGetter("death_attack") val deathAttack: Int? = null,
    val defense: Int? = null,
    val defensemod: String? = null,
    val imbueslots: Int? = null,
    val imbuements: String? = null,
    val enchantable: YesNo? = null,
    val enchanted: YesNo? = null,
    val range: String? = null,
    @get:JsonGetter("atk_mod") val attackModification: String? = null,
    @get:JsonGetter("hit_mod") val hitpointModification: String? = null,
    @get:JsonGetter("crithit_ch") val criticalHitChance: Percentage? = null,
    @get:JsonGetter("critextra_dmg") val criticalHitExtraDamage: Percentage? = null,
    @get:JsonGetter("manaleech_ch") val manaleechChance: Percentage? = null,
    @get:JsonGetter("manaleech_am") val manaleechAmount: Percentage? = null,
    @get:JsonGetter("hpleech_ch") val hitpointLeechChance: Percentage? = null,
    @get:JsonGetter("hpleech_am") val hitpointLeechAmount: Percentage? = null,
    val manacost: Int? = null,
    val damagetype: DamageElement? = null,
    val damagerange: String? = null,
    val upgradeclass: Int? = null,
    val attrib: String? = null,
    val charges: Int? = null,
    val armor: Int? = null,
    val resist: String? = null,
    val weight: BigDecimal? = null,
    val stackable: YesNo? = null,
    val marketable: YesNo? = null,
    val consumable: YesNo? = null,
    val regenseconds: Int? = null,
    val hangable: YesNo? = null,
    val duration: String? = null,
    val destructible: YesNo? = null,
    val rotatable: YesNo? = null,
    val mapcolor: Int? = null,
    val droppedby: MutableList<String>? = mutableListOf(),
    val value: String? = null,
    val storevalue: String? = null,
    val npcvalue: String? = null,
    val npcprice: String? = null,
    val npcvaluerook: String? = null,
    val npcpricerook: String? = null,
    val buyfrom: String? = null,
    val sellto: String? = null,
    val pricecurrency: String? = null,
    val fansite: String? = null,
    val location: String? = null,
    val notes2: String? = null,
) : WikiObject() {
    override fun fieldOrder(): MutableList<String> {
        return mutableListOf(
            "name",
            "article",
            "actualname",
            "plural",
            "itemid",
            "objectclass",
            "primarytype",
            "secondarytype",
            "tertiarytype",
            "flavortext",
            "words",
            "sounds",
            "implemented",
            "lightradius",
            "lightcolor",
            "volume",
            "destructable",
            "immobile",
            "walkable",
            "walkingspeed",
            "unshootable",
            "blockspath",
            "pickupable",
            "holdsliquid",
            "usable",
            "writable",
            "rewritable",
            "writechars",
            "levelrequired",
            "vocrequired",
            "mlrequired",
            "hands",
            "weapontype",
            "attack",
            "fire_attack",
            "earth_attack",
            "ice_attack",
            "energy_attack",
            "death_attack",
            "defense",
            "defensemod",
            "imbueslots",
            "imbuements",
            "enchantable",
            "enchanted",
            "range",
            "atk_mod",
            "hit_mod",
            "crithit_ch",
            "critextra_dmg",
            "manaleech_ch",
            "manaleech_am",
            "hpleech_ch",
            "hpleech_am",
            "manacost",
            "damagetype",
            "damagerange",
            "upgradeclass",
            "attrib",
            "charges",
            "armor",
            "resist",
            "weight",
            "stackable",
            "marketable",
            "consumable",
            "regenseconds",
            "hangable",
            "duration",
            "destructible",
            "rotatable",
            "mapcolor",
            "droppedby",
            "value",
            "storevalue",
            "npcvalue",
            "npcprice",
            "npcvaluerook",
            "npcpricerook",
            "buyfrom",
            "sellto",
            "pricecurrency",
            "fansite",
            "location",
            "notes",
            "notes2",
            "history",
            "status"
        )
    }

    override fun getTemplateType(): String {
        return InfoboxTemplate.OBJECT.templateName
    }
}
