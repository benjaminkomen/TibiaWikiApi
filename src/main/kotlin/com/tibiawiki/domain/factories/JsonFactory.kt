package com.tibiawiki.domain.factories

import com.google.common.base.Strings
import com.tibiawiki.domain.objects.HuntingPlaceSkills
import com.tibiawiki.domain.utils.TemplateUtils
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.regex.Pattern

/**
 * Conversion from infoboxPartOfArticle to JSON and back.
 */
@Component("wikiJsonFactory")
class JsonFactory {

    /**
     * Convert a String which consists of key-value pairs of infobox template parameters to a JSON object, or an empty
     * JSON object if the input was empty.
     */
    fun convertInfoboxPartOfArticleToJson(infoboxPartOfArticle: String?): JSONObject {
        val parametersAndValues = HashMap<String, String?>()

        if (infoboxPartOfArticle.isNullOrEmpty()) {
            return JSONObject()
        }

        val templateType = getTemplateType(infoboxPartOfArticle)
        var infoboxTemplatePartOfArticleSanitized = TemplateUtils.removeFirstAndLastLine(infoboxPartOfArticle)

        if (TEMPLATE_TYPE_HUNTING_PLACE == templateType) {
            val lowerLevelsOptional = TemplateUtils.extractLowerLevels(infoboxTemplatePartOfArticleSanitized)
            if (lowerLevelsOptional.isPresent) {
                parametersAndValues.putAll(lowerLevelsOptional.get())
                infoboxTemplatePartOfArticleSanitized = TemplateUtils.removeLowerLevels(infoboxTemplatePartOfArticleSanitized)
            }
        }

        parametersAndValues.putAll(TemplateUtils.splitInfoboxByParameter(infoboxTemplatePartOfArticleSanitized))
        parametersAndValues[TEMPLATE_TYPE] = templateType
        return enhanceJsonObject(JSONObject(parametersAndValues))
    }

    /**
     * Convert a String which consists of key-value pairs of loot2 template parameters to a JSON object, or an empty
     * JSON object if the input was empty.
     */
    fun convertLootPartOfArticleToJson(pageName: String, lootPartOfArticle: String?): JSONObject {
        if (lootPartOfArticle.isNullOrEmpty()) {
            return JSONObject()
        }

        val lootTemplatePartOfArticleSanitized = TemplateUtils.removeFirstAndLastLine(lootPartOfArticle)
        val parametersAndValues = HashMap(TemplateUtils.splitLootByParameter(lootTemplatePartOfArticleSanitized))
        parametersAndValues["pageName"] = pageName
        return enhanceLootJsonObject(JSONObject(parametersAndValues))
    }

    fun convertAllLootPartsOfArticleToJson(pageName: String, lootPartsOfArticle: Map<String, String>): JSONObject {
        if (lootPartsOfArticle.isEmpty()) {
            return JSONObject()
        }

        val result = JSONObject()
        lootPartsOfArticle.forEach { (key, value) ->
            result.put(key, convertLootPartOfArticleToJson(pageName, value))
        }
        return result
    }

    fun convertJsonToInfoboxPartOfArticle(jsonObject: JSONObject?, fieldOrder: List<String>): String {
        if (jsonObject == null || jsonObject.isEmpty) {
            return ""
        }

        if (!jsonObject.has(TEMPLATE_TYPE)) {
            LOG.error("Template type unknown for given json object: {}", jsonObject)
            return ""
        }

        val stringBuilder = StringBuilder()
        stringBuilder.append("{{Infobox ")
        stringBuilder.append(jsonObject.get(TEMPLATE_TYPE))

        if (jsonObject.get(TEMPLATE_TYPE) != TEMPLATE_TYPE_LOCATION &&
            jsonObject.get(TEMPLATE_TYPE) != TEMPLATE_TYPE_STREET
        ) {
            stringBuilder.append("|List={{{1|}}}|GetValue={{{GetValue|}}}")
        }

        stringBuilder.append("\n")
        constructKeyValuePairs(jsonObject, fieldOrder, stringBuilder)
        stringBuilder.append("}}").append("\n")
        return stringBuilder.toString()
    }

    private fun constructKeyValuePairs(jsonObject: JSONObject, fieldOrder: List<String>, sb: StringBuilder) {
        for (key in fieldOrder) {
            if (TEMPLATE_TYPE == key || !jsonObject.has(key)) {
                continue
            }

            val value = jsonObject.get(key)
            if (value is JSONArray) {
                when (key) {
                    SOUNDS -> sb.append(makeTemplateList(jsonObject, key, value, "Sound List"))
                    SPAWN_TYPE -> sb.append(makeCommaSeparatedStringList(jsonObject, key, value))
                    LOOT -> sb.append(makeLootTable(jsonObject, key, value))
                    DROPPED_BY -> sb.append(makeTemplateList(jsonObject, key, value, "Dropped By"))
                    ITEM_ID -> sb.append(makeCommaSeparatedStringList(jsonObject, key, value))
                    LOWER_LEVELS -> sb.append(makeSkillsTable(jsonObject, key, value, HuntingPlaceSkills.fieldOrder()))
                    else -> sb.append(makeCommaSeparatedStringList(jsonObject, key, value))
                }
            } else {
                val paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ')
                sb.append("| ")
                    .append(paddedKey)
                    .append(" = ")
                    .append(value)
                    .append("\n")
            }
        }
    }

    /**
     * Extracts template type from input. Allows cases of e.g. {{Infobox_Hunt|}} (with an underscore) or without an underscore.
     */
    fun getTemplateType(infoboxTemplatePartOfArticle: String?): String {
        return Optional.ofNullable(infoboxTemplatePartOfArticle)
            .map { Pattern.compile(INFOBOX_HEADER_PATTERN).matcher(it) }
            .filter { it.find() }
            .filter { it.groupCount() > 0 }
            .map { it.group(1) }
            .filter { it.isNotEmpty() }
            .orElseGet {
                LOG.warn("Template type could not be determined from string {}", infoboxTemplatePartOfArticle)
                UNKNOWN
            }
    }

    fun enhanceJsonObject(jsonObject: JSONObject): JSONObject {
        val templateType = if (jsonObject.has(TEMPLATE_TYPE)) {
            jsonObject.getString(TEMPLATE_TYPE)
        } else {
            UNKNOWN
        }

        val articleName = determineArticleName(jsonObject, templateType)

        if (jsonObject.has(SOUNDS)) {
            jsonObject.put(SOUNDS, makeSoundsArray(jsonObject.getString(SOUNDS), articleName))
        }
        if (jsonObject.has(SPAWN_TYPE)) {
            jsonObject.put(SPAWN_TYPE, JSONArray(TemplateUtils.splitByCommaAndTrim(jsonObject.getString(SPAWN_TYPE))))
        }
        if (jsonObject.has(LOOT) && TEMPLATE_TYPE_HUNTING_PLACE != templateType) {
            jsonObject.put(LOOT, makeLootTableArray(jsonObject.getString(LOOT)))
        }
        if (jsonObject.has(DROPPED_BY)) {
            jsonObject.put(DROPPED_BY, makeDroppedByArray(jsonObject.getString(DROPPED_BY), articleName))
        }
        if (jsonObject.has(ITEM_ID)) {
            jsonObject.put(ITEM_ID, JSONArray(TemplateUtils.splitByCommaAndTrim(jsonObject.getString(ITEM_ID))))
        }
        if (jsonObject.has(EFFECT_ID)) {
            jsonObject.put(EFFECT_ID, JSONArray(TemplateUtils.splitByCommaAndTrim(jsonObject.getString(EFFECT_ID))))
        }
        if (jsonObject.has(LOWER_LEVELS)) {
            jsonObject.put(LOWER_LEVELS, makeLowerLevelsArray(jsonObject.getString(LOWER_LEVELS)))
        }
        return jsonObject
    }

    /**
     * The input jsonObject has real key-value pairs such as version, kills and name, but also Loot lines which need to be
     * converted.
     */
    fun enhanceLootJsonObject(jsonObject: JSONObject): JSONObject {
        val enhancedJsonObject = JSONObject()
        val lootArray = JSONArray()
        val keyIterator = jsonObject.keys()

        while (keyIterator.hasNext()) {
            val key = keyIterator.next()
            val value = jsonObject.get(key)
            if (value is String) {
                if (Character.isLowerCase(key.codePointAt(0))) {
                    enhancedJsonObject.put(key, value)
                } else {
                    lootArray.put(makeLootEntry(key, value))
                }
            }
        }

        if (lootArray.length() > 0) {
            enhancedJsonObject.put("loot", lootArray)
        }
        return enhancedJsonObject
    }

    /**
     * We get a stringValue here which can be one of the following values:
     * - "times:25"
     * - "times:25, amount:1, total:25"
     */
    private fun makeLootEntry(key: String, stringValue: String): JSONObject {
        val lootEntry = JSONObject()
        lootEntry.put(ITEM_NAME, key)
        val matcher = Pattern.compile(LOOT_LINE_NAME_PATTERN).matcher(stringValue)
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                val timesAmountOrTotal = matcher.group(1)
                val splitToLabelAndNumber = timesAmountOrTotal.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                lootEntry.put(splitToLabelAndNumber[0], splitToLabelAndNumber[1])
            }
        }
        return lootEntry
    }

    /**
     * Usually the articleName is the value from the key 'name', but for books, locations or keys it is different.
     */
    fun determineArticleName(jsonObject: JSONObject?, templateType: String?): String {
        if (jsonObject == null || templateType.isNullOrEmpty()) {
            return UNKNOWN
        }

        return when (templateType) {
            TEMPLATE_TYPE_BOOK -> if (jsonObject.has("pagename")) jsonObject.getString("pagename") else UNKNOWN
            TEMPLATE_TYPE_LOCATION -> UNKNOWN
            TEMPLATE_TYPE_KEY -> if (jsonObject.has("number")) "Key " + jsonObject.getString("number") else UNKNOWN
            else -> if (jsonObject.has("name")) jsonObject.getString("name") else UNKNOWN
        }
    }

    private fun makeSoundsArray(soundsValue: String?, articleName: String): JSONArray {
        if (soundsValue != null && soundsValue.length > 2 && !soundsValue.contains("{{Sound List")) {
            LOG.warn("soundsValue '{}' from article '{}' does not contain Template:Sound List", soundsValue, articleName)
            return JSONArray()
        } else if (soundsValue != null && !soundsValue.contains("|")) {
            return JSONArray()
        }

        return Optional.ofNullable(soundsValue)
            .map { TemplateUtils.removeStartAndEndOfTemplate(it) }
            .map { Pattern.compile("\\|").split(it).toList() }
            .map { JSONArray(it) }
            .orElseGet { JSONArray() }
    }

    private fun makeLootTableArray(lootValue: String): JSONArray {
        val lootItemJsonObjects = ArrayList<JSONObject>()

        if (lootValue.matches(Regex("(\\{\\{Loot Table[\\s|]*}}|)"))) {
            return JSONArray()
        }

        val lootItemsPartOfLootTable = TemplateUtils.getBetweenOuterBalancedBrackets(lootValue, "{{Loot Table")
        if (lootItemsPartOfLootTable.isEmpty) {
            return JSONArray()
        }

        val lootItemsPartOfLootTableStripped = TemplateUtils.removeFirstAndLastLine(lootItemsPartOfLootTable.get())
        if (lootItemsPartOfLootTableStripped.length < 3) {
            return JSONArray()
        }

        val lootItemsList = Pattern.compile("(^|\n)(\\s|)\\|").split(lootItemsPartOfLootTableStripped)
        for (lootItemTemplate in lootItemsList) {
            if (lootItemTemplate.isEmpty()) {
                continue
            }
            val lootItem = TemplateUtils.removeStartAndEndOfTemplate(lootItemTemplate)
            if (lootItem == null) {
                LOG.error("Unable to create lootTableArray from lootValue: {}", lootValue)
                return JSONArray()
            }
            lootItemJsonObjects.add(makeLootItemJsonObject(Pattern.compile("\\|").split(lootItem).toList()))
        }

        return JSONArray(lootItemJsonObjects)
    }

    private fun makeLootItemJsonObject(splitLootItem: List<String>): JSONObject {
        val lootItemMap = HashMap<String, String>()
        for (lootItemPart in splitLootItem) {
            if (lootItemPart.lowercase().matches(Regex(RARITY_PATTERN))) {
                lootItemMap[RARITY] = lootItemPart
            } else if (lootItemPart.isNotBlank() && Character.isDigit(lootItemPart[0]) && !TEXT_PATTERN.matcher(lootItemPart).find()) {
                lootItemMap[AMOUNT] = lootItemPart
            } else {
                lootItemMap[ITEM_NAME] = lootItemPart
            }
        }
        return JSONObject(lootItemMap)
    }

    private fun makeDroppedByArray(droppedbyValue: String, articleName: String): JSONArray {
        if (droppedbyValue.length < 2 ||
            droppedbyValue.matches(Regex("[Nn]one(\\.|)")) ||
            legallyHasNoDroppedByTemplate(articleName) ||
            !droppedbyValue.contains("|")
        ) {
            return JSONArray()
        }
        check(droppedbyValue.contains("{{Dropped By")) {
            "droppedbyValue $droppedbyValue' from article '$articleName' does not contain Template:Dropped By"
        }
        val creatures = TemplateUtils.removeStartAndEndOfTemplate(droppedbyValue)
        return if (!creatures.isNullOrEmpty()) {
            JSONArray(Pattern.compile("\\|").split(creatures).toList())
        } else {
            JSONArray()
        }
    }

    private fun makeLowerLevelsArray(lowerLevelsValue: String): JSONArray {
        val infoboxHuntSkillsList = ArrayList<String>()
        val matcher = Pattern.compile("(?:(?:\\{\\{Infobox Hunt Skills(.*?)}})+)", Pattern.DOTALL)
            .matcher(lowerLevelsValue.trim())
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                infoboxHuntSkillsList.add(matcher.group(1).replace(Regex("^\\s+"), ""))
            }
        }

        val infoboxHuntSkillJsonObjects = infoboxHuntSkillsList.map { s ->
            JSONObject(HashMap(TemplateUtils.splitInfoboxByParameter(s)))
        }
        return JSONArray(infoboxHuntSkillJsonObjects)
    }

    private fun legallyHasNoDroppedByTemplate(name: String): Boolean {
        return ITEMS_WITH_NO_DROPPEDBY_LIST.contains(name)
    }

    private fun getMaxFieldLength(jsonObject: JSONObject): Int {
        return jsonObject.keySet().maxOfOrNull { it.length } ?: 0
    }

    private fun getMaxFieldLength(map: Map<*, *>): Int {
        return map.keys.filterIsInstance<String>().maxOfOrNull { it.length } ?: 0
    }

    private fun makeTemplateList(jsonObject: JSONObject, key: String, jsonArray: JSONArray, templateName: String): String {
        val paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ')
        val value = jsonArray.toList().joinToString("|") { it.toString() }
        return "| $paddedKey = {{$templateName|$value}}\n"
    }

    private fun makeCommaSeparatedStringList(jsonObject: JSONObject, key: String, jsonArray: JSONArray): String {
        val paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ')
        val value = jsonArray.toList().joinToString(", ") { it.toString() }
        return "| $paddedKey = $value\n"
    }

    private fun makeLootTable(jsonObject: JSONObject, key: String, jsonArray: JSONArray): String {
        val paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ')
        if (jsonArray.isEmpty) {
            return "| $paddedKey = {{Loot Table}}\n"
        }
        val value = jsonArray.toList().joinToString("\n |") { makeLootItem(it) }
        return "| $paddedKey = {{Loot Table\n |$value\n}}\n"
    }

    private fun makeSkillsTable(jsonObject: JSONObject, key: String, jsonArray: JSONArray, fieldOrder: List<String>): String {
        val result = StringBuilder("| ")
        val paddedKey = Strings.padEnd(key, getMaxFieldLength(jsonObject), ' ')
        result.append(paddedKey)
        result.append(" = \n")

        for (huntSkillsJsonObject in jsonArray.toList()) {
            result.append("    {{Infobox Hunt Skills\n")
            val map = huntSkillsJsonObject as Map<*, *>
            for (huntSkillsKey in fieldOrder) {
                val paddedHuntSkillsKey = Strings.padEnd(huntSkillsKey, getMaxFieldLength(map), ' ')
                val value = map[huntSkillsKey]
                if (value != null) {
                    result.append("    | ")
                        .append(paddedHuntSkillsKey)
                        .append(" = ")
                        .append(value)
                        .append("\n")
                }
            }
            result.append("    }}\n")
        }
        return result.toString()
    }

    private fun makeLootItem(obj: Any): String {
        val map = obj as Map<*, *>
        val result = StringBuilder("{{Loot Item")
        if (map.containsKey(AMOUNT)) {
            result.append("|").append(map[AMOUNT])
        }
        if (map.containsKey(ITEM_NAME)) {
            result.append("|").append(map[ITEM_NAME])
        }
        if (map.containsKey(RARITY)) {
            result.append("|").append(map[RARITY])
        }
        return result.append("}}").toString()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(JsonFactory::class.java)
        private const val TEMPLATE_TYPE = "templateType"
        const val TEMPLATE_TYPE_ACHIEVEMENT = "Achievement"
        const val TEMPLATE_TYPE_BOOK = "Book"
        const val TEMPLATE_TYPE_LOCATION = "Geography"
        private const val TEMPLATE_TYPE_HUNTING_PLACE = "Hunt"
        private const val TEMPLATE_TYPE_STREET = "Street"
        const val TEMPLATE_TYPE_KEY = "Key"
        private const val SOUNDS = "sounds"
        private const val SPAWN_TYPE = "spawntype"
        private const val LOOT = "loot"
        private const val DROPPED_BY = "droppedby"
        private const val ITEM_ID = "itemid"
        private const val EFFECT_ID = "effectid"
        private const val LOWER_LEVELS = "lowerlevels"
        private val ITEMS_WITH_NO_DROPPEDBY_LIST = listOf("Gold Coin", "Platinum Coin")
        private const val INFOBOX_HEADER_PATTERN = "\\{\\{Infobox[\\s|_](.*?)[|\\n]"
        private const val RARITY_PATTERN = "(always|common|uncommon|semi-rare|rare|very rare|extremely rare)(|\\?)"
        private const val LOOT_LINE_NAME_PATTERN = "(\\w+:[\\d-]+)"
        private val TEXT_PATTERN: Pattern = Pattern.compile("[a-zA-Z]")
        private const val UNKNOWN = "Unknown"
        private const val RARITY = "rarity"
        private const val AMOUNT = "amount"
        private const val ITEM_NAME = "itemName"
    }
}
