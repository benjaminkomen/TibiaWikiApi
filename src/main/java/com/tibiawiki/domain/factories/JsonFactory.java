package com.tibiawiki.domain.factories;

import com.tibiawiki.domain.utils.TemplateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Conversion from infoboxPartOfArticle to JSON.
 */
public class JsonFactory {

    private static final Logger log = LoggerFactory.getLogger(JsonFactory.class);
    private static final String OBJECT_TYPE = "type";
    private static final String OBJECT_TYPE_BOOK = "Book";
    private static final String OBJECT_TYPE_LOCATION = "Geography";
    private static final String OBJECT_TYPE_HUNTING_PLACE = "Hunt";
    private static final String SOUNDS = "sounds";
    private static final String SPAWN_TYPE = "spawntype";
    private static final String LOOT = "loot";
    private static final String DROPPED_BY = "droppedby";
    private static final String ITEM_ID = "itemid";
    private static final String LOWER_LEVELS = "lowerlevels";
    private static final List ITEMS_WITH_NO_DROPPEDBY_LIST = Arrays.asList("Gold Coin", "Platinum Coin");
    private static final String INFOBOX_HEADER_PATTERN = "\\{\\{Infobox[\\s|_](.*?)[\\||\\n]";
    private static final String UNKNOWN = "Unknown";

    /**
     * Convert a String which consists of key-value pairs of infobox template parameters to a JSON object, or an empty
     * JSON object if the input was empty.
     */
    public JSONObject convertInfoboxPartOfArticleToJson(final String infoboxPartOfArticle) {
        Map<String, String> parametersAndValues = new HashMap<>();

        if (infoboxPartOfArticle == null || "".equals(infoboxPartOfArticle)) {
            return new JSONObject();
        }

        final String objectType = getTemplateType(infoboxPartOfArticle);
        String infoboxTemplatePartOfArticleSanitized = TemplateUtils.removeFirstAndLastLine(infoboxPartOfArticle);

        if (OBJECT_TYPE_HUNTING_PLACE.equals(objectType)) {
            Optional<Map<String, String>> lowerLevelsOptional = TemplateUtils.extractLowerLevels(infoboxTemplatePartOfArticleSanitized);

            if (lowerLevelsOptional.isPresent()) {
                parametersAndValues.putAll(lowerLevelsOptional.get());
                infoboxTemplatePartOfArticleSanitized = TemplateUtils.removeLowerLevels(infoboxTemplatePartOfArticleSanitized);
            }
        }

        parametersAndValues.putAll(TemplateUtils.splitByParameter(infoboxTemplatePartOfArticleSanitized));
        parametersAndValues.put(OBJECT_TYPE, objectType);
        return enhanceJsonObject(new JSONObject(parametersAndValues));
    }

    /**
     * An alternative implementation which uses a regex. This relaxes the strict requirement of {{Infobox Hunt|}} and
     * allows cases of e.g. {{Infobox_Hunt|}}, in other words, with an underscore.
     */
    private String getTemplateType(final String infoboxTemplatePartOfArticle) {
        String templateType = null;
        Pattern pattern = Pattern.compile(INFOBOX_HEADER_PATTERN);
        Matcher matcher = pattern.matcher(infoboxTemplatePartOfArticle);
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                templateType = matcher.group(1);
                break;
            }
        }
        if (templateType != null && !"".equals(templateType)) {
            return templateType;
        } else {
            log.warn("Template type could not be determined from string {}", infoboxTemplatePartOfArticle);
            return UNKNOWN;
        }
    }

    private JSONObject enhanceJsonObject(JSONObject jsonObject) {
        if (jsonObject.has(OBJECT_TYPE)) {

            assert (jsonObject.has("name")) : "parameter name not found in jsonObject:" + jsonObject.toString(2);

            final String articleName;
            final String objectType = jsonObject.getString(OBJECT_TYPE);

            switch (objectType) {
                case OBJECT_TYPE_BOOK:
                    articleName = jsonObject.getString("pagename");
                    break;
                case OBJECT_TYPE_LOCATION:
                    articleName = UNKNOWN;
                    break;
                default:
                    articleName = jsonObject.getString("name");
            }

            if (jsonObject.has(SOUNDS)) {
                String soundsValue = jsonObject.getString(SOUNDS);
                JSONArray soundsArray = makeSoundsArray(soundsValue, articleName);
                jsonObject.put(SOUNDS, soundsArray);
            }

            if (jsonObject.has(SPAWN_TYPE)) {
                String spawntypeValue = jsonObject.getString(SPAWN_TYPE);
                JSONArray spawntypeArray = new JSONArray(TemplateUtils.splitByCommaAndTrim(spawntypeValue));
                jsonObject.put(SPAWN_TYPE, spawntypeArray);
            }

            if (jsonObject.has(LOOT) && !OBJECT_TYPE_HUNTING_PLACE.equals(objectType)) {
                String lootValue = jsonObject.getString(LOOT);
                JSONArray lootTableArray = makeLootTableArray(lootValue);
                jsonObject.put(LOOT, lootTableArray);
            }

            if (jsonObject.has(DROPPED_BY)) {
                String droppedbyValue = jsonObject.getString(DROPPED_BY);
                JSONArray droppedbyArray = makeDroppedByArray(droppedbyValue, articleName);
                jsonObject.put(DROPPED_BY, droppedbyArray);
            }

            if (jsonObject.has(ITEM_ID)) {
                String itemIdValue = jsonObject.getString(ITEM_ID);
                JSONArray itemIdArray = new JSONArray(TemplateUtils.splitByCommaAndTrim(itemIdValue));
                jsonObject.put(ITEM_ID, itemIdArray);
            }

            if (jsonObject.has(LOWER_LEVELS)) {
                String lowerLevelsValue = jsonObject.getString(LOWER_LEVELS);
                JSONArray lowerLevelsArray = makeLowerLevelsArray(lowerLevelsValue, articleName);
                jsonObject.put(LOWER_LEVELS, lowerLevelsArray);
            }
        }
        return jsonObject;
    }

    private JSONArray makeSoundsArray(String soundsValue, String articleName) {
        if (soundsValue.length() < 2) {
            return new JSONArray();
        }
        assert (soundsValue.contains("{{Sound List")) : "soundsValue '" + soundsValue + "' from article '" + articleName +
                "' does not contain Template:Sound List";
        String sounds = TemplateUtils.removeStartAndEndOfTemplate(soundsValue);
        List<String> splitLines = Arrays.asList(Pattern.compile("\\|").split(sounds));
        return new JSONArray(splitLines);
    }

    private JSONArray makeLootTableArray(String lootValue) {
        List<JSONObject> lootItemJsonObjects = new ArrayList<>();

        if (lootValue.matches("(\\{\\{Loot Table(\\||\\s|[\\n\\s]+|)}}|)")) {
            return new JSONArray();
        }

        String lootItemsPartOfLootTable = TemplateUtils.getBetweenOuterBalancedBrackets(lootValue, "{{Loot Table");
        lootItemsPartOfLootTable = TemplateUtils.removeFirstAndLastLine(lootItemsPartOfLootTable);

        if (lootItemsPartOfLootTable.length() < 3) {
            return new JSONArray();
        }

        List<String> lootItemsList = Arrays.asList(Pattern.compile("(^|\n)(\\s|)\\|").split(lootItemsPartOfLootTable));

        for (String lootItemTemplate : lootItemsList) {
            if (lootItemTemplate.length() < 1) {
                continue;
            }
            String lootItem = TemplateUtils.removeStartAndEndOfTemplate(lootItemTemplate);
            if (lootItem == null) {
                log.error("Unable to create lootTableArray from lootValue: {}", lootValue);
                return new JSONArray();
            }
            List<String> splitLootItem = Arrays.asList(Pattern.compile("\\|").split(lootItem));
            JSONObject lootItemJsonObject = makeLootItemJsonObject(splitLootItem);
            lootItemJsonObjects.add(lootItemJsonObject);
        }

        return new JSONArray(lootItemJsonObjects);
    }

    private JSONObject makeLootItemJsonObject(List<String> splitLootItem) {
        Map<String, String> lootItemMap = new HashMap<>();

        for (String lootItemPart : splitLootItem) {
            if (Character.isUpperCase(lootItemPart.charAt(0))) {
                lootItemMap.put("itemName", lootItemPart);
            } else if (Character.isDigit(lootItemPart.charAt(0))) {
                lootItemMap.put("amount", lootItemPart);
            } else if (Character.isLowerCase(lootItemPart.charAt(0))) {
                lootItemMap.put("rarity", lootItemPart);
            } else {
                log.warn("The text '{}' in Template:Loot Item could not be identified as item name, amount or rarity.", lootItemPart);
            }
        }
        return new JSONObject(lootItemMap);
    }

    private JSONArray makeDroppedByArray(String droppedbyValue, String articleName) {
        if (droppedbyValue.length() < 2 || droppedbyValue.matches("(N|n)one(\\.|)") || legallyHasNoDroppedByTemplate(articleName)) {
            return new JSONArray();
        }
        assert (droppedbyValue.contains("{{Dropped By")) : "droppedbyValue " +
                droppedbyValue + "' from article '" + articleName + "' does not contain Template:Dropped By";
        String creatures = TemplateUtils.removeStartAndEndOfTemplate(droppedbyValue);
        List<String> splitLines = Arrays.asList(Pattern.compile("\\|").split(creatures));
        return new JSONArray(splitLines);
    }

    // @todo finish this method
    private JSONArray makeLowerLevelsArray(String lowerLevelsValue, String articleName) {
        List<String> infoboxHuntSkillsList = new ArrayList<>();

        String lowerLevelsValueTrimmed = lowerLevelsValue.trim();

        Pattern pattern = Pattern.compile("(?:(?:\\{\\{Infobox Hunt Skills(.*?)}})+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(lowerLevelsValueTrimmed);
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                String infoboxHuntSkills = matcher.group(1);
                infoboxHuntSkillsList.add(infoboxHuntSkills);
            }
        }


        return new JSONArray();
    }

    private boolean legallyHasNoDroppedByTemplate(String name) {
        return ITEMS_WITH_NO_DROPPEDBY_LIST.contains(name);
    }
}
