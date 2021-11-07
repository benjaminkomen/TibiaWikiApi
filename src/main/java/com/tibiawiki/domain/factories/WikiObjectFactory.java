package com.tibiawiki.domain.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.Book;
import com.tibiawiki.domain.objects.Building;
import com.tibiawiki.domain.objects.Corpse;
import com.tibiawiki.domain.objects.Creature;
import com.tibiawiki.domain.objects.Effect;
import com.tibiawiki.domain.objects.HuntingPlace;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Create a WikiObject from a previously constructed JSONObject, and back.
 */
@Component
public class WikiObjectFactory {

    private static final Logger log = LoggerFactory.getLogger(WikiObjectFactory.class);
    private static final String TEMPLATE_TYPE = "templateType";
    private static final String TEMPLATE_TYPE_ACHIEVEMENT = "Achievement";
    private static final String TEMPLATE_TYPE_BOOK = "Book";
    private static final String TEMPLATE_TYPE_BUILDING = "Building";
    private static final String TEMPLATE_TYPE_CORPSE = "Corpse";
    private static final String TEMPLATE_TYPE_CREATURE = "Creature";
    private static final String TEMPLATE_TYPE_EFFECT = "Effect";
    private static final String TEMPLATE_TYPE_LOCATION = "Geography";
    private static final String TEMPLATE_TYPE_HUNTING_PLACE = "Hunt";
    private static final String TEMPLATE_TYPE_MOUNT = "Mount";
    private static final String TEMPLATE_TYPE_ITEM = "Item";
    private static final String TEMPLATE_TYPE_KEY = "Key";
    private static final String TEMPLATE_TYPE_NPC = "NPC";
    private static final String TEMPLATE_TYPE_OBJECT = "Object";
    private static final String TEMPLATE_TYPE_OUTFIT = "Outfit";
    private static final String TEMPLATE_TYPE_QUEST = "Quest";
    private static final String TEMPLATE_TYPE_SPELL = "Spell";
    private static final String TEMPLATE_TYPE_STREET = "Street";

    private final ObjectMapper objectMapper;

    @Autowired
    public WikiObjectFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Stream<WikiObject> createWikiObjects(List<JSONObject> jsonObjects) {
        return createWikiObjects(jsonObjects.toArray(new JSONObject[0]));
    }

    public Stream<WikiObject> createWikiObjects(JSONObject[] jsonObjects) {
        return Arrays.stream(jsonObjects)
                .map(this::createWikiObject);
    }

    /**
     * Creates a WikiObject from a JSONObject.
     * The reverse is achieved by {@link #createJSONObject} when saving the JSON back to the wiki.
     */
    public WikiObject createWikiObject(JSONObject wikiObjectJson) {
        WikiObject wikiObject = null;
        String templateType;

        try {
            templateType = (String) wikiObjectJson.get(TEMPLATE_TYPE);
        } catch (JSONException e) {
            log.error("WikiObjectJson does not contain any templateType.");
            return new WikiObject.WikiObjectImpl();
        }

        switch (templateType) {
            case TEMPLATE_TYPE_ACHIEVEMENT -> wikiObject = mapJsonToObject(wikiObjectJson, Achievement.class);
            case TEMPLATE_TYPE_BOOK -> wikiObject = mapJsonToObject(wikiObjectJson, Book.class);
            case TEMPLATE_TYPE_BUILDING -> wikiObject = mapJsonToObject(wikiObjectJson, Building.class);
            case TEMPLATE_TYPE_CORPSE -> wikiObject = mapJsonToObject(wikiObjectJson, Corpse.class);
            case TEMPLATE_TYPE_CREATURE -> wikiObject = mapJsonToObject(wikiObjectJson, Creature.class);
            case TEMPLATE_TYPE_EFFECT -> wikiObject = mapJsonToObject(wikiObjectJson, Effect.class);
            case TEMPLATE_TYPE_LOCATION -> wikiObject = mapJsonToObject(wikiObjectJson, Location.class);
            case TEMPLATE_TYPE_HUNTING_PLACE -> wikiObject = mapJsonToObject(wikiObjectJson, HuntingPlace.class);
            case TEMPLATE_TYPE_ITEM, TEMPLATE_TYPE_OBJECT -> wikiObject = mapJsonToObject(wikiObjectJson, TibiaObject.class);
            case TEMPLATE_TYPE_KEY -> wikiObject = mapJsonToObject(wikiObjectJson, Key.class);
            case TEMPLATE_TYPE_MOUNT -> wikiObject = mapJsonToObject(wikiObjectJson, Mount.class);
            case TEMPLATE_TYPE_NPC -> wikiObject = mapJsonToObject(wikiObjectJson, NPC.class);
            case TEMPLATE_TYPE_OUTFIT -> wikiObject = mapJsonToObject(wikiObjectJson, Outfit.class);
            case TEMPLATE_TYPE_QUEST -> wikiObject = mapJsonToObject(wikiObjectJson, Quest.class);
            case TEMPLATE_TYPE_SPELL -> wikiObject = mapJsonToObject(wikiObjectJson, Spell.class);
            case TEMPLATE_TYPE_STREET -> wikiObject = mapJsonToObject(wikiObjectJson, Street.class);
            default -> log.warn("object type '{}' not supported, terminating..", templateType);
        }

        return wikiObject;
    }

    /**
     * Creates an Article from a WikiObject, for saving to the wiki.
     * The reverse is achieved by {@link #createWikiObject(JSONObject)} when reading from the wiki.
     */
    public JSONObject createJSONObject(WikiObject wikiObject, String templateType) {
        final Map<String, Object> wikiObjectAsMap = objectMapper.convertValue(wikiObject, Map.class);
        wikiObjectAsMap.put(TEMPLATE_TYPE, templateType);
        return new JSONObject(wikiObjectAsMap);
    }

    private <T> T mapJsonToObject(JSONObject wikiObjectJson, Class<T> clazz) {
        try {
            return objectMapper.readValue(wikiObjectJson.toString(), clazz);
        } catch (IOException e) {
            log.error("Unable to convert json to {} object.", clazz.toString(), e);
        }
        return null;
    }
}
