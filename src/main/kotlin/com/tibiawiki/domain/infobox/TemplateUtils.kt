package com.tibiawiki.domain.infobox

import org.slf4j.LoggerFactory
import java.util.HashMap
import java.util.LinkedList
import java.util.Objects
import java.util.regex.Pattern

object TemplateUtils {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private const val REGEX_PARAMETER_INFOBOX_LINE = """\|\s+?([A-Za-z0-9_\-]+)\s*?="""
    private const val REGEX_PARAMETER_LOOT_LINE = """\|(.+?)\s*?(=|,)"""
    private const val REGEX_PARAMETER_LOWER_LEVELS = """\|\s+?lowerlevels\s*?=((?:.*?\{\{.*?}})+)"""
    private const val REGEX_PARAMETER_LOWER_LEVELS_REMOVE = """\|\s+?lowerlevels\s*?=((.*?\{\{.*?}})+)"""
    private const val LOWER_LEVELS = "lowerlevels"

    fun getBetweenOuterBalancedBrackets(text: String, start: String): String? {
        return getStartingAndEndingCurlyBrackets(text, start)
            ?.let { (start, end) -> text.substring(start, end) }
    }

    /**
     * @param text  to search in
     * @param start string which denoted the start of the balanced brackets
     * @return two strings, the first is the substring of the provided text before the start of the balanced brackets,
     * the second is the substring after the start of the balanced brackets.
     */
    fun getBeforeAndAfterOuterBalancedBrackets(text: String, start: String): Pair<String, String>? {
        return getStartingAndEndingCurlyBrackets(text, start)
            ?.let { (start, end) -> Pair(text.substring(0, start), text.substring(end)) }
    }

    /**
     * Remove the first line of the input string, that is, between the start of the string and the first occurrence
     * of a \n character.
     * Remove the last line of the input string, that is, everything after the last occurrence of two }} characters. The
     * reason we are not looking for the last \n character is that some templates may end with | notes =\n}} and then
     * removing the last \n character also removes the value of the key "notes".
     */
    fun removeFirstAndLastLine(text: String?): String {
        return text
            ?.let { it.substring(it.indexOf('\n') + 1) } // remove first line
            ?.let {
                it.substring(
                    0,
                    if (it.lastIndexOf('\n') > -1) it.lastIndexOf("}}") else 0
                )
            } // remove last line
            ?: ""
    }

    fun removeStartAndEndOfTemplate(text: String): String? {
        if (text.length < 2) {
            return null
        }
        val startOfTemplate = text.indexOf('|') + 1
        val endOfTemplate = text.indexOf("}}")
        if (endOfTemplate >= 0) {
            return text.substring(startOfTemplate, endOfTemplate).trim { it <= ' ' }
        }
        logger.error("Could not remove start and end of template.")
        return null
    }

    fun splitInfoboxByParameter(infoboxTemplatePartOfArticle: String?): MutableMap<String, String?> {
        return splitByParameter(infoboxTemplatePartOfArticle, REGEX_PARAMETER_INFOBOX_LINE)
    }

    fun splitLootByParameter(lootTemplatePartOfArticle: String?): MutableMap<String, String?> {
        return splitByParameter(lootTemplatePartOfArticle, REGEX_PARAMETER_LOOT_LINE)
    }

    fun splitByParameter(infoboxTemplatePartOfArticle: String?, regex: String): MutableMap<String, String?> {
        if (infoboxTemplatePartOfArticle == null || "" == infoboxTemplatePartOfArticle) {
            return mutableMapOf()
        }
        val keyValuePair: MutableMap<String, String?> = HashMap()

        // first get keys
        val keys: MutableList<String> = LinkedList()
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(infoboxTemplatePartOfArticle)
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                val key = matcher.group(1)
                keys.add(key)
            }
        }
        val values = listOf(*pattern.split(infoboxTemplatePartOfArticle))

        // sanitize values to get rid of empty Strings
        val sanitizedValues = values
            .filter { it.isNotEmpty() }
            .map { obj -> obj.trim { it <= ' ' } }
            .map { it.replace("\n$".toRegex(), "") }
            .map { if ("" == it) null else it } // transform empty strings to null values

        if (keys.size != sanitizedValues.size && logger.isErrorEnabled) {
            val endLength = infoboxTemplatePartOfArticle.length.coerceAtMost(200)
            logger.error(
                "Amount of keys and values don't match for article starting with: {}",
                infoboxTemplatePartOfArticle.substring(0, endLength).replace("\\n".toRegex(), "")
            )
            return mutableMapOf()
        }

        // put keys and values into map
        for (i in keys.indices) {
            val key = keys[i]
            val value = sanitizedValues[i]
            keyValuePair[key] = value
        }
        return keyValuePair
    }

    fun splitByCommaAndTrim(input: String?): List<String> {
        return input?.split("")
            ?.filter { Objects.nonNull(it) }
            ?.filter { i -> i.trim { it <= ' ' }.isNotEmpty() }
            ?.map { it.split(",") }
            ?.flatMap { lst -> lst.map { obj -> obj.trim { it <= ' ' } } }
            ?: emptyList()
    }

    fun extractLowerLevels(infoboxTemplatePartOfArticleSanitized: String?): Map<String, String>? {
        if (infoboxTemplatePartOfArticleSanitized == null || "" == infoboxTemplatePartOfArticleSanitized) {
            return null
        }
        val keyValuePair: MutableMap<String, String> = HashMap()
        val pattern = Pattern.compile(REGEX_PARAMETER_LOWER_LEVELS, Pattern.DOTALL)
        val matcher = pattern.matcher(infoboxTemplatePartOfArticleSanitized)
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                val value = matcher.group(1)
                keyValuePair[LOWER_LEVELS] = value
            }
        }
        return if (keyValuePair.isEmpty()) null else keyValuePair
    }

    fun removeLowerLevels(infoboxTemplatePartOfArticleSanitized: String?): String {
        return infoboxTemplatePartOfArticleSanitized
            ?.let { Pattern.compile(REGEX_PARAMETER_LOWER_LEVELS_REMOVE, Pattern.DOTALL).matcher(it) }
            ?.replaceAll("")
            ?: ""
    }

    /**
     * @param text  the text to search in
     * @param start the start String which denotes the start of the curly brackets
     * @return a tuple of two integers: the index of the start of the curly brackets and an index of the end of
     * the curly brackets
     */
    private fun getStartingAndEndingCurlyBrackets(text: String, start: String): Pair<Int, Int>? {
        val startingCurlyBrackets = text.indexOf(start)
        if (startingCurlyBrackets < 0) {
            return null // could not find text in string
        }
        var endingCurlyBrackets = 0
        var openBracketsCounter = 0
        var currentChar: Char
        for (i in startingCurlyBrackets until text.length) {
            currentChar = text[i]
            if ('{' == currentChar) {
                openBracketsCounter++
            }
            if ('}' == currentChar) {
                openBracketsCounter--
            }
            if (openBracketsCounter == 0) {
                endingCurlyBrackets = i + 1
                break
            }
        }
        return Pair(startingCurlyBrackets, endingCurlyBrackets)
    }
}
