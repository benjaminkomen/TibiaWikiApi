package com.tibiawiki.domain.factories;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.tibiawiki.domain.objects.*;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Create a WikiObject from a previously constructed JSONObject, and back.
 */
public class WikiObjectFactory {

    private static final Logger log = LoggerFactory.getLogger(WikiObjectFactory.class);
    private static final String OBJECT_TYPE = "type";
    private static final String OBJECT_TYPE_ACHIEVEMENT = "Achievement";
    private static final String OBJECT_TYPE_BOOK = "Book";
    private static final String OBJECT_TYPE_BUILDING = "Building";
    private static final String OBJECT_TYPE_CORPSE = "Corpse";
    private static final String OBJECT_TYPE_CREATURE = "Creature";
    private static final String OBJECT_TYPE_EFFECT = "Effect";
    private static final String OBJECT_TYPE_LOCATION = "Geography";
    private static final String OBJECT_TYPE_HUNTING_PLACE = "Hunt";
    private static final String OBJECT_TYPE_MOUNT = "Mount";
    private static final String OBJECT_TYPE_KEY = "Key";

    private ObjectMapper objectMapper;

    public WikiObjectFactory() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
        final WikiObject wikiObject;
        final String objectType = (String) wikiObjectJson.get(OBJECT_TYPE);

        switch (objectType) {
            case OBJECT_TYPE_ACHIEVEMENT:
                wikiObject = mapJsonToObject(wikiObjectJson, Achievement.class);
                break;
            case OBJECT_TYPE_BOOK:
                wikiObject = mapJsonToObject(wikiObjectJson, Book.class);
                break;
            case OBJECT_TYPE_BUILDING:
                wikiObject = mapJsonToObject(wikiObjectJson, Building.class);
                break;
            case OBJECT_TYPE_CORPSE:
                wikiObject = mapJsonToObject(wikiObjectJson, Corpse.class);
                break;
            case OBJECT_TYPE_CREATURE:
                wikiObject = mapJsonToObject(wikiObjectJson, Creature.class);
                break;
            case OBJECT_TYPE_EFFECT:
                wikiObject = mapJsonToObject(wikiObjectJson, Effect.class);
                break;
            case OBJECT_TYPE_LOCATION:
                wikiObject = mapJsonToObject(wikiObjectJson, Location.class);
                break;
            case OBJECT_TYPE_HUNTING_PLACE:
                wikiObject = mapJsonToObject(wikiObjectJson, HuntingPlace.class);
                break;
            case OBJECT_TYPE_KEY:
                wikiObject = mapJsonToObject(wikiObjectJson, Key.class);
                break;
            case OBJECT_TYPE_MOUNT:
                wikiObject = mapJsonToObject(wikiObjectJson, Mount.class);
                break;
            default:
                log.warn("object type '{}' not supported, terminating..", objectType);
                return null;
        }

        return wikiObject;
    }

    /**
     * Creates an Article from a WikiObject, for saving to the wiki.
     * The reverse is achieved by {@link #createWikiObject(JSONObject)} when reading from the wiki.
     */
    public static Article createJSONObject(WikiBot wikiBot, WikiObject wikiObject) {
        return new Article(wikiBot, createSimpleArticle(wikiBot, wikiObject));
    }

    private static SimpleArticle createSimpleArticle(WikiBot wikiBot, WikiObject wikiObject) {
        SimpleArticle simpleArticle = new SimpleArticle();
        simpleArticle.setEditor(wikiBot.getUserinfo().getUsername());
        simpleArticle.setEditTimestamp(new Date(ZonedDateTime.now().toEpochSecond()));
        simpleArticle.setTitle(wikiObject.getName());
        simpleArticle.setText(createArticleText(wikiObject));
        return simpleArticle;
    }

    protected static String createArticleText(WikiObject wikiObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("{{Infobox ");
        sb.append(wikiObject.getClassName());
        sb.append("|List={{{1|}}}|GetValue={{{GetValue|}}}").append("\n");

        int maxKeyLength = wikiObject.maxFieldSize() + 2;

        for (String key : wikiObject.getFieldNames()) {
            Object value = wikiObject.getValue(key);
            String paddedKey = Strings.padEnd(key, maxKeyLength, ' ');
            sb.append("| ")
                    .append(paddedKey)
                    .append(" = ")
                    .append(value)
                    .append("\n");
        }

        sb.append("}}").append("\n");
        return sb.toString();
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
