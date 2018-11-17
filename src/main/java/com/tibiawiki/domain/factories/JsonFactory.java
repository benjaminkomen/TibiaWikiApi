package com.tibiawiki.domain.factories;

import com.google.common.base.Strings;
import com.tibiawiki.domain.utils.TemplateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Conversion from infoboxPartOfArticle to JSON and back.
 */
@Component
public class JsonFactory {

    private static final Logger LOG = LoggerFactory.getLogger(JsonFactory.class);
    private static final String TEMPLATE_TYPE = "templateType";
    protected static final String TEMPLATE_TYPE_ACHIEVEMENT = "Achievement";
    protected static final String TEMPLATE_TYPE_BOOK = "Book";
    protected static final String TEMPLATE_TYPE_LOCATION = "Geography";
    private static final String TEMPLATE_TYPE_HUNTING_PLACE = "Hunt";
    protected static final String TEMPLATE_TYPE_KEY = "Key";
    private static final String SOUNDS = "sounds";
    private static final String SPAWN_TYPE = "spawntype";
    private static final String LOOT = "loot";
    private static final String DROPPED_BY = "droppedby";
    private static final String ITEM_ID = "itemid";
    private static final String LOWER_LEVELS = "lowerlevels";
    private static final List ITEMS_WITH_NO_DROPPEDBY_LIST = Arrays.asList("Gold Coin", "Platinum Coin");
    private static final String INFOBOX_HEADER_PATTERN = "\\{\\{Infobox[\\s|_](.*?)[\\||\\n]";
    private static final String RARITY_PATTERN = "(always|common|uncommon|semi-rare|rare|very rare|extremely rare)(|\\?)";
    private static final String UNKNOWN = "Unknown";

    /**
     * Convert a String which consists of key-value pairs of infobox template parameters to a JSON object, or an empty
     * JSON object if the input was empty.
     */
    @NotNull
    public JSONObject convertInfoboxPartOfArticleToJson(@Nullable final String infoboxPartOfArticle) {
        Map<String, String> parametersAndValues = new HashMap<>();

        if (infoboxPartOfArticle == null || "".equals(infoboxPartOfArticle)) {
            return new JSONObject();
        }

        final String templateType = getTemplateType(infoboxPartOfArticle);
        String infoboxTemplatePartOfArticleSanitized = TemplateUtils.removeFirstAndLastLine(infoboxPartOfArticle);

        // Do something special for Infobox Hunt input, to process the lowerlevels parameter
        if (TEMPLATE_TYPE_HUNTING_PLACE.equals(templateType)) {
            Optional<Map<String, String>> lowerLevelsOptional = TemplateUtils.extractLowerLevels(infoboxTemplatePartOfArticleSanitized);

            if (lowerLevelsOptional.isPresent()) {
                parametersAndValues.putAll(lowerLevelsOptional.get());
                infoboxTemplatePartOfArticleSanitized = TemplateUtils.removeLowerLevels(infoboxTemplatePartOfArticleSanitized);
            }
        }

        parametersAndValues.putAll(TemplateUtils.splitByParameter(infoboxTemplatePartOfArticleSanitized));
        parametersAndValues.put(TEMPLATE_TYPE, templateType);
        return enhanceJsonObject(new JSONObject(parametersAndValues));
    }

    @NotNull
    public String convertJsonToInfoboxPartOfArticle(@Nullable JSONObject jsonObject, List<String> fieldOrder) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return "";
        }

        if (!jsonObject.has(TEMPLATE_TYPE)) {
            LOG.error("Template type unknown for given json object: {}", jsonObject);
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{{Infobox ");
        sb.append(jsonObject.get(TEMPLATE_TYPE));
        sb.append("|List={{{1|}}}|GetValue={{{GetValue|}}}").append("\n");

        int maxFieldLength = jsonObject.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        for (String key : fieldOrder) {

            // don't add the metadata parameter templateType to the output
            if (TEMPLATE_TYPE.equals(key)) {
                continue;
            }

            // simply skip fields we don't have, in most cases this is legit, an object doesn't need to have all fields
            // of its class
            if (!jsonObject.has(key)) {
                continue;
            }

            Object value = jsonObject.get(key);

            if (value instanceof JSONArray) {

                if (SOUNDS.equals(key)) {
                    // do the opposite of makeSoundsArray()
                } else if (SPAWN_TYPE.equals(key)) {

                } else if (LOOT.equals(key)) {

                } else if (DROPPED_BY.equals(key)) {

                } else if (ITEM_ID.equals(key)) {

                } else if (LOWER_LEVELS.equals(key)) {

                } else {
                    // just convert to a comma-separated list of array values
                }

            } else if (value instanceof JSONObject) {

            } else {
                String paddedKey = Strings.padEnd(key, maxFieldLength, ' ');
                sb.append("| ")
                        .append(paddedKey)
                        .append(" = ")
                        .append(value)
                        .append("\n");
            }
        }

        sb.append("}}").append("\n");
        return sb.toString();
    }

    /**
     * Extracts template type from input. Allows cases of e.g. {{Infobox_Hunt|}} (with an underscore) or without an underscore.
     */
    @NotNull
    protected String getTemplateType(@Nullable final String infoboxTemplatePartOfArticle) {
        return Optional.ofNullable(infoboxTemplatePartOfArticle)
                .map(s -> Pattern.compile(INFOBOX_HEADER_PATTERN).matcher(s))
                .filter(Matcher::find)
                .filter(m -> m.groupCount() > 0)
                .map(m -> m.group(1))
                .filter(s -> !"".equals(s))
                .orElseGet(() -> {
                    LOG.warn("Template type could not be determined from string {}", infoboxTemplatePartOfArticle);
                    return UNKNOWN;
                });
    }

    @NotNull
    protected JSONObject enhanceJsonObject(@NotNull JSONObject jsonObject) {

        final String templateType = Optional.of(jsonObject)
                .filter(j -> j.has(TEMPLATE_TYPE))
                .map(j -> j.getString(TEMPLATE_TYPE))
                .orElse(UNKNOWN);

        final String articleName = determineArticleName(jsonObject, templateType);

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

        if (jsonObject.has(LOOT) && !TEMPLATE_TYPE_HUNTING_PLACE.equals(templateType)) {
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
            JSONArray lowerLevelsArray = makeLowerLevelsArray(lowerLevelsValue);
            jsonObject.put(LOWER_LEVELS, lowerLevelsArray);
        }

        return jsonObject;
    }

    /**
     * Usually the articleName is the value from the key 'name', but for books, locations or keys it is different.
     */
    @NotNull
    protected String determineArticleName(@Nullable JSONObject jsonObject, @Nullable String templateType) {
        if (jsonObject == null || templateType == null || "".equals(templateType)) {
            return UNKNOWN;
        }

        String articleName;
        switch (templateType) {
            case TEMPLATE_TYPE_BOOK:
                articleName = jsonObject.has("pagename") ? jsonObject.getString("pagename") : UNKNOWN;
                break;
            case TEMPLATE_TYPE_LOCATION:
                articleName = UNKNOWN;
                break;
            case TEMPLATE_TYPE_KEY:
                articleName = jsonObject.has("number") ? "Key " + jsonObject.getString("number") : UNKNOWN;
                break;
            default:
                articleName = jsonObject.has("name") ? jsonObject.getString("name") : UNKNOWN;
        }
        return articleName;
    }

    @NotNull
    private JSONArray makeSoundsArray(@Nullable String soundsValue, @NotNull String articleName) {
        if (soundsValue != null && soundsValue.length() > 2 && !soundsValue.contains("{{Sound List")) {
            LOG.error("soundsValue '{}' from article '{}' does not contain Template:Sound List", soundsValue, articleName);
            return new JSONArray();
        }

        return Optional.ofNullable(soundsValue)
                .map(TemplateUtils::removeStartAndEndOfTemplate)
                .map(s -> Arrays.asList(Pattern.compile("\\|").split(s)))
                .map(JSONArray::new)
                .orElseGet(JSONArray::new);
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
                LOG.error("Unable to create lootTableArray from lootValue: {}", lootValue);
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
            if (lootItemPart.toLowerCase().matches(RARITY_PATTERN)) {
                lootItemMap.put("rarity", lootItemPart);
            } else if (Character.isDigit(lootItemPart.charAt(0))) {
                lootItemMap.put("amount", lootItemPart);
            } else {
                lootItemMap.put("itemName", lootItemPart);
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

    private JSONArray makeLowerLevelsArray(String lowerLevelsValue) {
        List<String> infoboxHuntSkillsList = new ArrayList<>();

        String lowerLevelsValueTrimmed = lowerLevelsValue.trim();

        Pattern pattern = Pattern.compile("(?:(?:\\{\\{Infobox Hunt Skills(.*?)}})+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(lowerLevelsValueTrimmed);
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                String infoboxHuntSkills = matcher.group(1);
                infoboxHuntSkillsList.add(infoboxHuntSkills.replaceAll("^\\s+", ""));
            }
        }

        final List<JSONObject> infoboxHuntSkillJsonObjects = infoboxHuntSkillsList.stream()
                .map(s -> new JSONObject(new HashMap<>(TemplateUtils.splitByParameter(s))))
                .collect(Collectors.toList());

        return new JSONArray(infoboxHuntSkillJsonObjects);
    }

    private boolean legallyHasNoDroppedByTemplate(String name) {
        return ITEMS_WITH_NO_DROPPEDBY_LIST.contains(name);
    }
}
