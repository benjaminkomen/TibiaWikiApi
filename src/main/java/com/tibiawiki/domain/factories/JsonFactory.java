package com.tibiawiki.domain.factories;

import com.google.common.base.Strings;
import com.tibiawiki.domain.objects.HuntingPlaceSkills;
import com.tibiawiki.domain.utils.TemplateUtils;
import lombok.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private static final String TEMPLATE_TYPE_STREET = "Street";
    protected static final String TEMPLATE_TYPE_KEY = "Key";
    private static final String SOUNDS = "sounds";
    private static final String SPAWN_TYPE = "spawntype";
    private static final String LOOT = "loot";
    private static final String DROPPED_BY = "droppedby";
    private static final String ITEM_ID = "itemid";
    private static final String EFFECT_ID = "effectid";
    private static final String LOWER_LEVELS = "lowerlevels";
    private static final List<String> ITEMS_WITH_NO_DROPPEDBY_LIST = Arrays.asList("Gold Coin", "Platinum Coin");
    private static final String INFOBOX_HEADER_PATTERN = "\\{\\{Infobox[\\s|_](.*?)[\\||\\n]";
    private static final String RARITY_PATTERN = "(always|common|uncommon|semi-rare|rare|very rare|extremely rare)(|\\?)";
    private static final String LOOT_LINE_NAME_PATTERN = "(\\w+:[\\d-]+)";
    private static final String UNKNOWN = "Unknown";
    private static final String RARITY = "rarity";
    private static final String AMOUNT = "amount";
    private static final String ITEM_NAME = "itemName";

    /**
     * Convert a String which consists of key-value pairs of infobox template parameters to a JSON object, or an empty
     * JSON object if the input was empty.
     */
    @NonNull
    public JSONObject convertInfoboxPartOfArticleToJson(final String infoboxPartOfArticle) {
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

        parametersAndValues.putAll(TemplateUtils.splitInfoboxByParameter(infoboxTemplatePartOfArticleSanitized));
        parametersAndValues.put(TEMPLATE_TYPE, templateType);
        return enhanceJsonObject(new JSONObject(parametersAndValues));
    }

    /**
     * Convert a String which consists of key-value pairs of loot2 template parameters to a JSON object, or an empty
     * JSON object if the input was empty.
     */
    @NonNull
    public JSONObject convertLootPartOfArticleToJson(String pageName, final String lootPartOfArticle) {
        if (lootPartOfArticle == null || "".equals(lootPartOfArticle)) {
            return new JSONObject();
        }

        String lootTemplatePartOfArticleSanitized = TemplateUtils.removeFirstAndLastLine(lootPartOfArticle);

        Map<String, String> parametersAndValues = new HashMap<>(TemplateUtils.splitLootByParameter(lootTemplatePartOfArticleSanitized));
        parametersAndValues.put("pageName", pageName);
        return enhanceLootJsonObject(new JSONObject(parametersAndValues));
    }

    @NonNull
    public JSONObject convertAllLootPartsOfArticleToJson(String pageName, Map<String, String> lootPartsOfArticle) {

        if (lootPartsOfArticle.isEmpty()) {
            return new JSONObject();
        }

        var result = new JSONObject();

        lootPartsOfArticle.forEach((k, v) -> {
            var value = convertLootPartOfArticleToJson(pageName, v);
            result.put(k, value);
        });

        return result;
    }

    @NonNull
    public String convertJsonToInfoboxPartOfArticle(JSONObject jsonObject, List<String> fieldOrder) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return "";
        }

        if (!jsonObject.has(TEMPLATE_TYPE)) {
            LOG.error("Template type unknown for given json object: {}", jsonObject);
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{{Infobox ");
        stringBuilder.append(jsonObject.get(TEMPLATE_TYPE));

        // Don't add this line with Location and Street templates
        if (!Objects.equals(jsonObject.get(TEMPLATE_TYPE), TEMPLATE_TYPE_LOCATION) &&
                !Objects.equals(jsonObject.get(TEMPLATE_TYPE), TEMPLATE_TYPE_STREET)) {
            stringBuilder.append("|List={{{1|}}}|GetValue={{{GetValue|}}}");
        }

        stringBuilder.append("\n");

        constructKeyValuePairs(jsonObject, fieldOrder, stringBuilder);

        stringBuilder.append("}}").append("\n");
        return stringBuilder.toString();
    }

    private void constructKeyValuePairs(@NonNull JSONObject jsonObject, List<String> fieldOrder, StringBuilder sb) {
        for (String key : fieldOrder) {

            // don't add the metadata parameter templateType to the output
            // simply skip fields we don't have, in most cases this is legit, an object doesn't need to have all fields
            // of its class
            if (TEMPLATE_TYPE.equals(key) || !jsonObject.has(key)) {
                continue;
            }

            Object value = jsonObject.get(key);

            if (value instanceof JSONArray) {

                if (SOUNDS.equals(key)) {
                    sb.append(makeTemplateList(jsonObject, key, (JSONArray) value, "Sound List"));
                } else if (SPAWN_TYPE.equals(key)) {
                    sb.append(makeCommaSeparatedStringList(jsonObject, key, (JSONArray) value));
                } else if (LOOT.equals(key)) {
                    sb.append(makeLootTable(jsonObject, key, (JSONArray) value));
                } else if (DROPPED_BY.equals(key)) {
                    sb.append(makeTemplateList(jsonObject, key, (JSONArray) value, "Dropped By"));
                } else if (ITEM_ID.equals(key)) {
                    sb.append(makeCommaSeparatedStringList(jsonObject, key, (JSONArray) value));
                } else if (LOWER_LEVELS.equals(key)) {
                    sb.append(makeSkillsTable(jsonObject, key, (JSONArray) value, HuntingPlaceSkills.fieldOrder()));
                } else {
                    sb.append(makeCommaSeparatedStringList(jsonObject, key, (JSONArray) value));
                }
            } else {
                String paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ');
                sb.append("| ")
                        .append(paddedKey)
                        .append(" = ")
                        .append(value)
                        .append("\n");
            }
        }
    }

    /**
     * Extracts template type from input. Allows cases of e.g. {{Infobox_Hunt|}} (with an underscore) or without an underscore.
     */
    @NonNull
    protected String getTemplateType(final String infoboxTemplatePartOfArticle) {
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

    @NonNull
    protected JSONObject enhanceJsonObject(@NonNull JSONObject jsonObject) {

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

        if (jsonObject.has(EFFECT_ID)) {
            String effectIdValue = jsonObject.getString(EFFECT_ID);
            JSONArray effectIdArray = new JSONArray(TemplateUtils.splitByCommaAndTrim(effectIdValue));
            jsonObject.put(EFFECT_ID, effectIdArray);
        }

        if (jsonObject.has(LOWER_LEVELS)) {
            String lowerLevelsValue = jsonObject.getString(LOWER_LEVELS);
            JSONArray lowerLevelsArray = makeLowerLevelsArray(lowerLevelsValue);
            jsonObject.put(LOWER_LEVELS, lowerLevelsArray);
        }

        return jsonObject;
    }

    /**
     * The input jsonObject has real key-value pairs such as version, kills and name, but also Loot lines which need to be
     * converted.
     */
    @NonNull
    protected JSONObject enhanceLootJsonObject(@NonNull JSONObject jsonObject) {

        JSONObject enhancedJsonObject = new JSONObject();
        JSONArray lootArray = new JSONArray();

        final Iterator<String> keyIterator = jsonObject.keys();

        // iterate over json object entries
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Object value = jsonObject.get(key);

            if (value instanceof String) {
                String stringValue = (String) value;

                // do not modify normal keys such as version, kills and name. We assume they are always lowercase
                if (Character.isLowerCase(key.codePointAt(0))) {
                    enhancedJsonObject.put(key, stringValue);
                } else {
                    lootArray.put(makeLootEntry(key, stringValue));
                }
            }
        }

        if (lootArray.length() > 0) {
            enhancedJsonObject.put("loot", lootArray);
        }

        return enhancedJsonObject;
    }

    /**
     * We get a stringValue here which can be one of the following values:
     * - "times:25"
     * - "times:25, amount:1, total:25"
     */
    private JSONObject makeLootEntry(String key, String stringValue) {
        JSONObject lootEntry = new JSONObject();

        lootEntry.put("itemName", key);

        Pattern pattern = Pattern.compile(LOOT_LINE_NAME_PATTERN);
        Matcher matcher = pattern.matcher(stringValue);
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                String timesAmountOrTotal = matcher.group(1);
                String[] splitToLabelAndNumber = timesAmountOrTotal.split(":");
                lootEntry.put(splitToLabelAndNumber[0], splitToLabelAndNumber[1]);
            }
        }

        return lootEntry;
    }

    /**
     * Usually the articleName is the value from the key 'name', but for books, locations or keys it is different.
     */
    @NonNull
    protected String determineArticleName(JSONObject jsonObject, String templateType) {
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

    @NonNull
    private JSONArray makeSoundsArray(String soundsValue, @NonNull String articleName) {
        if (soundsValue != null && soundsValue.length() > 2 && !soundsValue.contains("{{Sound List")) {
            LOG.warn("soundsValue '{}' from article '{}' does not contain Template:Sound List", soundsValue, articleName);
            return new JSONArray();
        } else if (soundsValue != null && !soundsValue.contains("|")) {
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

        var lootItemsPartOfLootTable = TemplateUtils.getBetweenOuterBalancedBrackets(lootValue, "{{Loot Table");

        if (lootItemsPartOfLootTable.isEmpty()) {
            return new JSONArray();
        }

        var lootItemsPartOfLootTableStripped = TemplateUtils.removeFirstAndLastLine(lootItemsPartOfLootTable.get());

        if (lootItemsPartOfLootTableStripped.length() < 3) {
            return new JSONArray();
        }

        List<String> lootItemsList = Arrays.asList(Pattern.compile("(^|\n)(\\s|)\\|").split(lootItemsPartOfLootTableStripped));

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
                lootItemMap.put(RARITY, lootItemPart);
            } else if (Character.isDigit(lootItemPart.charAt(0))) {
                lootItemMap.put(AMOUNT, lootItemPart);
            } else {
                lootItemMap.put(ITEM_NAME, lootItemPart);
            }
        }
        return new JSONObject(lootItemMap);
    }

    private JSONArray makeDroppedByArray(String droppedbyValue, String articleName) {
        if (droppedbyValue.length() < 2 || droppedbyValue.matches("[Nn]one(\\.|)") ||
                legallyHasNoDroppedByTemplate(articleName) || !droppedbyValue.contains("|")) {
            return new JSONArray();
        }
        assert (droppedbyValue.contains("{{Dropped By")) : "droppedbyValue " +
                droppedbyValue + "' from article '" + articleName + "' does not contain Template:Dropped By";
        String creatures = TemplateUtils.removeStartAndEndOfTemplate(droppedbyValue);

        if (creatures != null && creatures.length() > 0) {
            List<String> splitLines = Arrays.asList(Pattern.compile("\\|").split(creatures));
            return new JSONArray(splitLines);
        } else {
            return new JSONArray();
        }

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
                .map(s -> new JSONObject(new HashMap<>(TemplateUtils.splitInfoboxByParameter(s))))
                .collect(Collectors.toList());

        return new JSONArray(infoboxHuntSkillJsonObjects);
    }

    private boolean legallyHasNoDroppedByTemplate(String name) {
        return ITEMS_WITH_NO_DROPPEDBY_LIST.contains(name);
    }

    private int getMaxFieldLength(@NonNull JSONObject jsonObject) {
        return jsonObject.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

    private int getMaxFieldLength(@NonNull Map map) {
        return map.keySet().stream()
                .filter(String.class::isInstance)
                .mapToInt(s -> String.valueOf(s).length())
                .max()
                .orElse(0);
    }

    @NonNull
    private String makeTemplateList(JSONObject jsonObject, String key, JSONArray jsonArray, String templateName) {
        final String paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ');
        final String value = jsonArray.toList().stream()
                .map(String::valueOf)
                .collect(Collectors.joining("|"));

        return "| " + paddedKey + " = {{" + templateName + "|" + value + "}}\n";
    }

    private String makeCommaSeparatedStringList(JSONObject jsonObject, String key, JSONArray jsonArray) {
        final String paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ');
        final String value = jsonArray.toList().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        return "| " + paddedKey + " = " + value + "\n";
    }

    private String makeLootTable(JSONObject jsonObject, String key, JSONArray jsonArray) {
        final String paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ');

        if (jsonArray.isEmpty()) {
            return "| " + paddedKey + " = {{Loot Table}}\n";
        }

        final String value = jsonArray.toList().stream()
                .map(this::makeLootItem)
                .collect(Collectors.joining("\n |"));

        return "| " + paddedKey + " = {{Loot Table\n |" + value + "\n}}\n";
    }

    private String makeSkillsTable(JSONObject jsonObject, String key, JSONArray jsonArray, List<String> fieldOrder) {
        final StringBuilder result = new StringBuilder("| ");
        final String paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ');
        result.append(paddedKey);
        result.append(" = \n");

        for (Object huntSkillsJsonObject : jsonArray.toList()) {
            result.append("    {{Infobox Hunt Skills\n");

            Map map = (Map) huntSkillsJsonObject;

            for (String huntSkillsKey : fieldOrder) {
                String paddedHuntSkillsKey = Strings.padEnd(huntSkillsKey, getMaxFieldLength(map), ' ');
                Object value = map.get(huntSkillsKey);

                if (value != null) {
                    result.append("    | ")
                            .append(paddedHuntSkillsKey)
                            .append(" = ")
                            .append(value)
                            .append("\n");
                }
            }

            result.append("    }}\n");
        }

        return result.toString();
    }

    private String makeLootItem(Object obj) {
        Map map = (Map) obj;
        StringBuilder result = new StringBuilder("{{Loot Item");
        if (map.containsKey(AMOUNT)) {
            result.append("|").append(map.get(AMOUNT));
        }
        if (map.containsKey(ITEM_NAME)) {
            result.append("|").append(map.get(ITEM_NAME));
        }
        if (map.containsKey(RARITY)) {
            result.append("|").append(map.get(RARITY));
        }
        return result.append("}}").toString();
    }
}
