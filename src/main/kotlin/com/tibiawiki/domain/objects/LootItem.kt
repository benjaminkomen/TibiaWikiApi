package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.Rarity

data class LootItem(
    val itemName: String? = null,
    val amount: String? = null,
    val rarity: Rarity? = null
)
