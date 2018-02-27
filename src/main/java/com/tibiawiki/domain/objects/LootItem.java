package com.tibiawiki.domain.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LootItem {

    private String itemName;
    private String amount;
    private String rarity;

}
