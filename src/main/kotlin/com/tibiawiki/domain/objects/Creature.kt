package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonProperty
import com.tibiawiki.domain.enums.Article
import com.tibiawiki.domain.enums.AttackType
import com.tibiawiki.domain.enums.BestiaryClass
import com.tibiawiki.domain.enums.BestiaryLevel
import com.tibiawiki.domain.enums.BestiaryOccurrence
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Spawntype
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo

data class Creature(
    override val name: String? = null,
    override val article: Article? = null,
    override val actualname: String? = null,
    override val plural: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    @JsonProperty("race_id") val raceId: String? = null,
    @JsonProperty("hp") val hitPoints: String? = null,
    @JsonProperty("exp") val experiencePoints: String? = null,
    val armor: String? = null,
    val mitigation: String? = null,
    val summon: String? = null,
    val convince: String? = null,
    val illusionable: YesNo? = null,
    val creatureclass: String? = null,
    val primarytype: String? = null,
    val secondarytype: String? = null,
    val bestiaryclass: BestiaryClass? = null,
    val bestiarylevel: BestiaryLevel? = null,
    val occurrence: BestiaryOccurrence? = null,
    val attacktype: AttackType? = null,
    val usespells: YesNo? = null,
    val spawntype: List<Spawntype>? = null,
    val isboss: YesNo? = null,
    val bosstiaryclass: String? = null,
    val cooldown: String? = null,
    val isarenaboss: YesNo? = null,
    val isevent: YesNo? = null,
    val abilities: String? = null,
    val usedelements: String? = null,
    val maxdmg: String? = null,
    val maxbattlelength: String? = null,
    val lightradius: Int? = null,
    val lightcolor: Int? = null,
    val pushable: YesNo? = null,
    val pushobjects: YesNo? = null,
    val walksaround: String? = null,
    val walksthrough: String? = null,
    val paraimmune: YesNo? = null,
    val senseinvis: YesNo? = null,
    val physicalDmgMod: Percentage? = null,
    val earthDmgMod: Percentage? = null,
    val fireDmgMod: Percentage? = null,
    val deathDmgMod: Percentage? = null,
    val energyDmgMod: Percentage? = null,
    val holyDmgMod: Percentage? = null,
    val iceDmgMod: Percentage? = null,
    @JsonProperty("healMod") val healMod: Percentage? = null,
    val hpDrainDmgMod: Percentage? = null,
    val drownDmgMod: Percentage? = null,
    val bestiaryname: String? = null,
    val bestiarytext: String? = null,
    val sounds: List<String>? = null,
    val behaviour: String? = null,
    val runsat: String? = null,
    val speed: String? = null,
    val strategy: String? = null,
    val location: String? = null,
    val loot: List<LootItem>? = null
) : WikiObject(
    name = name,
    article = article,
    actualname = actualname,
    plural = plural,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.CREATURE.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "article", "actualname", "plural", "hp", "exp", "armor", "mitigation", "summon", "convince",
            "illusionable", "creatureclass", "primarytype", "secondarytype", "bestiaryclass", "bestiarylevel",
            "occurrence", "attacktype", "usespells", "spawntype", "isboss", "bosstiaryclass", "cooldown",
            "isarenaboss", "isevent", "abilities", "usedelements", "maxdmg", "maxbattlelength", "lightradius",
            "lightcolor", "pushable", "pushobjects", "walksaround", "walksthrough", "paraimmune", "senseinvis",
            "physicalDmgMod", "earthDmgMod", "fireDmgMod", "deathDmgMod", "energyDmgMod", "holyDmgMod", "iceDmgMod",
            "hpDrainDmgMod", "drownDmgMod", "healMod", "bestiaryname", "bestiarytext", "sounds", "implemented",
            "race_id", "notes", "behaviour", "runsat", "speed", "strategy", "location", "loot", "history", "status"
        )
    }
}
