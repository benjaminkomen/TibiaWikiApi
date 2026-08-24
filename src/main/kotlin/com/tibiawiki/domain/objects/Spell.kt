package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonProperty
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.SpellSubclass
import com.tibiawiki.domain.enums.SpellType
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.Vocation
import com.tibiawiki.domain.enums.YesNo

data class Spell(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val type: SpellType? = null,
    val subclass: SpellSubclass? = null,
    val runegroup: SpellSubclass? = null,
    val damagetype: String? = null,
    val wheelspell: YesNo? = null,
    val passivespell: YesNo? = null,
    val words: String? = null,
    val mana: Int? = null,
    val cooldown: Int? = null,
    val cooldown2: Int? = null,
    val cooldown3: Int? = null,
    val cooldowngroup: Int? = null,
    val cooldowngroup2: Int? = null,
    val secondarygroup: SpellSubclass? = null,
    val levelrequired: Int? = null,
    val premium: YesNo? = null,
    val promotion: YesNo? = null,
    val soul: Int? = null,
    val zoltanonly: YesNo? = null,
    val partyspell: YesNo? = null,
    val specialspell: YesNo? = null,
    val conjurespell: YesNo? = null,
    val voc: String? = null,
    @JsonProperty("d-abd") val druidAbDendriel: String? = null,
    @JsonProperty("d-ank") val druidAnkrahmun: String? = null,
    @JsonProperty("d-car") val druidCarlin: String? = null,
    @JsonProperty("d-dar") val druidDarashia: String? = null,
    @JsonProperty("d-edr") val druidEdron: String? = null,
    @JsonProperty("d-kaz") val druidKazordoon: String? = null,
    @JsonProperty("d-lib") val druidLibertyBay: String? = null,
    @JsonProperty("d-por") val druidPortHope: String? = null,
    @JsonProperty("d-rat") val druidRathleton: String? = null,
    @JsonProperty("d-sva") val druidSvargrond: String? = null,
    @JsonProperty("d-tha") val druidThais: String? = null,
    @JsonProperty("d-ven") val druidVenore: String? = null,
    @JsonProperty("d-yal") val druidYalahar: String? = null,
    @JsonProperty("k-abd") val knightAbDendriel: String? = null,
    @JsonProperty("k-ank") val knightAnkrahmun: String? = null,
    @JsonProperty("k-car") val knightCarlin: String? = null,
    @JsonProperty("k-dar") val knightDarashia: String? = null,
    @JsonProperty("k-edr") val knightEdron: String? = null,
    @JsonProperty("k-kaz") val knightKazordoon: String? = null,
    @JsonProperty("k-lib") val knightLibertyBay: String? = null,
    @JsonProperty("k-por") val knightPortHope: String? = null,
    @JsonProperty("k-rat") val knightRathleton: String? = null,
    @JsonProperty("k-sva") val knightSvargrond: String? = null,
    @JsonProperty("k-tha") val knightThais: String? = null,
    @JsonProperty("k-ven") val knightVenore: String? = null,
    @JsonProperty("k-yal") val knightYalahar: String? = null,
    @JsonProperty("p-abd") val paladinAbDendriel: String? = null,
    @JsonProperty("p-ank") val paladinAnkrahmun: String? = null,
    @JsonProperty("p-car") val paladinCarlin: String? = null,
    @JsonProperty("p-dar") val paladinDarashia: String? = null,
    @JsonProperty("p-edr") val paladinEdron: String? = null,
    @JsonProperty("p-kaz") val paladinKazordoon: String? = null,
    @JsonProperty("p-lib") val paladinLibertyBay: String? = null,
    @JsonProperty("p-por") val paladinPortHope: String? = null,
    @JsonProperty("p-rat") val paladinRathleton: String? = null,
    @JsonProperty("p-sva") val paladinSvargrond: String? = null,
    @JsonProperty("p-tha") val paladinThais: String? = null,
    @JsonProperty("p-ven") val paladinVenore: String? = null,
    @JsonProperty("p-yal") val paladinYalahar: String? = null,
    @JsonProperty("s-abd") val sorcererAbDendriel: String? = null,
    @JsonProperty("s-ank") val sorcererAnkrahmun: String? = null,
    @JsonProperty("s-car") val sorcererCarlin: String? = null,
    @JsonProperty("s-dar") val sorcererDarashia: String? = null,
    @JsonProperty("s-edr") val sorcererEdron: String? = null,
    @JsonProperty("s-kaz") val sorcererKazordoon: String? = null,
    @JsonProperty("s-lib") val sorcererLibertyBay: String? = null,
    @JsonProperty("s-por") val sorcererPortHope: String? = null,
    @JsonProperty("s-rat") val sorcererRathleton: String? = null,
    @JsonProperty("s-sva") val sorcererSvargrond: String? = null,
    @JsonProperty("s-tha") val sorcererThais: String? = null,
    @JsonProperty("s-ven") val sorcererVenore: String? = null,
    @JsonProperty("s-yal") val sorcererYalahar: String? = null,
    val spellcost: Int? = null,
    val spellid: Int? = null,
    val libraryname: String? = null,
    val librarytext: String? = null,
    val basepower: Int? = null,
    val effect: String? = null,
    val animation: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    /**
     * Typed vocations parsed from wiki `voc` text (including Monk).
     * Not part of [fieldOrder]; write-back keeps the original `voc` string.
     */
    @get:JsonProperty("vocations")
    val vocations: List<Vocation>
        get() = Vocation.parseVoc(voc)

    override fun getTemplateType(): String {
        return InfoboxTemplate.SPELL.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "type", "subclass", "runegroup", "damagetype", "wheelspell", "passivespell", "words",
            "mana", "cooldown", "cooldown2", "cooldown3", "cooldowngroup", "cooldowngroup2", "secondarygroup",
            "levelrequired", "premium", "promotion", "soul", "zoltanonly", "partyspell", "specialspell",
            "conjurespell", "voc", "d-abd", "d-ank", "d-car", "d-dar", "d-edr", "d-kaz", "d-lib", "d-por",
            "d-rat", "d-sva", "d-tha", "d-ven", "d-yal", "k-abd", "k-ank", "k-car", "k-dar", "k-edr",
            "k-kaz", "k-lib", "k-por", "k-rat", "k-sva", "k-tha", "k-ven", "k-yal", "p-abd", "p-ank",
            "p-car", "p-dar", "p-edr", "p-kaz", "p-lib", "p-por", "p-rat", "p-sva", "p-tha", "p-ven",
            "p-yal", "s-abd", "s-ank", "s-car", "s-dar", "s-edr", "s-kaz", "s-lib", "s-por", "s-rat",
            "s-sva", "s-tha", "s-ven", "s-yal", "spellcost", "spellid", "libraryname", "librarytext",
            "basepower", "implemented", "effect", "notes", "animation", "history", "status"
        )
    }
}
