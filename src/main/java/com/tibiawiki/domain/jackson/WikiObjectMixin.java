package com.tibiawiki.domain.jackson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.Book;
import com.tibiawiki.domain.objects.Building;
import com.tibiawiki.domain.objects.Corpse;
import com.tibiawiki.domain.objects.Creature;
import com.tibiawiki.domain.objects.Effect;
import com.tibiawiki.domain.objects.HuntingPlace;
import com.tibiawiki.domain.objects.Item;
import com.tibiawiki.domain.objects.Key;
import com.tibiawiki.domain.objects.Location;
import com.tibiawiki.domain.objects.Mount;
import com.tibiawiki.domain.objects.NPC;
import com.tibiawiki.domain.objects.Outfit;
import com.tibiawiki.domain.objects.Quest;
import com.tibiawiki.domain.objects.Spell;
import com.tibiawiki.domain.objects.Street;
import com.tibiawiki.domain.objects.TibiaObject;
import com.tibiawiki.domain.objects.WikiObject;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "templateType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Achievement.class, name = "Achievement"),
        @JsonSubTypes.Type(value = Book.class, name = "Book"),
        @JsonSubTypes.Type(value = Building.class, name = "Building"),
        @JsonSubTypes.Type(value = Corpse.class, name = "Corpse"),
        @JsonSubTypes.Type(value = Creature.class, name = "Creature"),
        @JsonSubTypes.Type(value = Effect.class, name = "Effect"),
        @JsonSubTypes.Type(value = HuntingPlace.class, name = "Hunt"),
        @JsonSubTypes.Type(value = Item.class, name = "Item"),
        @JsonSubTypes.Type(value = Key.class, name = "Key"),
        @JsonSubTypes.Type(value = Location.class, name = "Geography"),
        @JsonSubTypes.Type(value = Mount.class, name = "Mount"),
        @JsonSubTypes.Type(value = NPC.class, name = "NPC"),
        @JsonSubTypes.Type(value = Outfit.class, name = "Outfit"),
        @JsonSubTypes.Type(value = Quest.class, name = "Quest"),
        @JsonSubTypes.Type(value = Spell.class, name = "Spell"),
        @JsonSubTypes.Type(value = Street.class, name = "Street"),
        @JsonSubTypes.Type(value = TibiaObject.class, name = "Object")
})
public abstract class WikiObjectMixin extends WikiObject {


}
