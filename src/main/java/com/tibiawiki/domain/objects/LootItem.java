package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.Rarity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LootItem {

    private String itemName;
    private String amount;
    private Rarity rarity;
}
