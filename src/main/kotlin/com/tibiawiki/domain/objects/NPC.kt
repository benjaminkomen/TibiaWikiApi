package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.City
import com.tibiawiki.domain.enums.Gender
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo
import java.math.BigDecimal

data class NPC(
    override val name: String? = null,
    override val actualname: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val job: String? = null,
    val job2: String? = null,
    val job3: String? = null,
    val job4: String? = null,
    val job5: String? = null,
    val job6: String? = null,
    val location: String? = null,
    val city: City? = null,
    val city2: City? = null,
    val subarea: String? = null,
    val street: String? = null,
    val posx: BigDecimal? = null,
    val posy: BigDecimal? = null,
    val posz: Int? = null,
    val geolabel: String? = null,
    val posx2: BigDecimal? = null,
    val posy2: BigDecimal? = null,
    val posz2: Int? = null,
    val geolabel2: String? = null,
    val posx3: BigDecimal? = null,
    val posy3: BigDecimal? = null,
    val posz3: Int? = null,
    val geolabel3: String? = null,
    val posx4: BigDecimal? = null,
    val posy4: BigDecimal? = null,
    val posz4: Int? = null,
    val geolabel4: String? = null,
    val posx5: BigDecimal? = null,
    val posy5: BigDecimal? = null,
    val posz5: Int? = null,
    val geolabel5: String? = null,
    val posx6: BigDecimal? = null,
    val posy6: BigDecimal? = null,
    val posz6: Int? = null,
    val geolabel6: String? = null,
    val posx7: BigDecimal? = null,
    val posy7: BigDecimal? = null,
    val posz7: Int? = null,
    val geolabel7: String? = null,
    val gender: Gender? = null,
    val race: String? = null,
    val buysell: YesNo? = null,
    val buys: String? = null,
    val sells: String? = null,
    val sounds: List<String>? = null
) : WikiObject(
    name = name,
    actualname = actualname,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.NPC.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "actualname", "job", "job2", "job3", "job4", "job5", "job6", "location", "city",
            "city2", "subarea", "street", "posx", "posy", "posz", "geolabel", "posx2", "posy2", "posz2", "geolabel2",
            "posx3", "posy3", "posz3", "geolabel3", "posx4", "posy4", "posz4", "geolabel4", "posx5", "posy5", "posz5",
            "geolabel5", "posx6", "posy6", "posz6", "geolabel6", "posx7", "posy7", "posz7", "geolabel7", "gender",
            "race", "buysell", "sounds", "implemented", "notes", "history", "status"
        )
    }
}
