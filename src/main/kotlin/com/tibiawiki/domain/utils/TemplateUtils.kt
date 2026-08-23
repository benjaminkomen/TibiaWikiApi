package com.tibiawiki.domain.utils

import io.vavr.Tuple
import io.vavr.Tuple2
import org.slf4j.LoggerFactory
import java.util.Optional
import java.util.regex.Pattern

object TemplateUtils {
    private val log = LoggerFactory.getLogger(TemplateUtils::class.java)
    private const val REGEX_PARAMETER_INFOBOX_LINE = "\\|\\s+?([A-Za-z0-9_\\-]+)\\s*?="
    private const val REGEX_PARAMETER_LOOT_LINE = "\\|(.+?)\\s*?(=|,)"
    private const val REGEX_PARAMETER_LOWER_LEVELS = "\\|\\s+?lowerlevels\\s*?=((?:.*?\\{\\{.*?}})+)"
    private const val REGEX_PARAMETER_LOWER_LEVELS_REMOVE = "\\|\\s+?lowerlevels\\s*?=((.*?\\{\\{.*?}})+)"
    private const val LOWER_LEVELS = "lowerlevels"

    @JvmStatic
    fun getBetweenOuterBalancedBrackets(text: String, start: String): Optional<String> {
        return Optional.ofNullable(getStartingAndEndingCurlyBrackets(text, start))
            .map { text.substring(it._1(), it._2()) }
    }

    /**
     * @return two strings, the first is the substring of the provided text before the start of the balanced brackets,
     * the second is the substring after the start of the balanced brackets.
     */
    @JvmStatic
    fun getBeforeAndAfterOuterBalancedBrackets(text: String, start: String): Optional<Tuple2<String, String>> {
        return Optional.ofNullable(getStartingAndEndingCurlyBrackets(text, start))
            .map { Tuple.of(text.substring(0, it._1()), text.substring(it._2())) }
    }

    /**
     * Remove the first line of the input string, that is, between the start of the string and the first occurrence
     * of a \n character.
     * Remove the last line of the input string, that is, everything after the last occurrence of two }} characters.
     */
    @JvmStatic
    fun removeFirstAndLastLine(text: String?): String {
        return text
            ?.let { it.substring(it.indexOf('\n') + 1) }
            ?.let { t ->
                val lastNewline = t.lastIndexOf('\n')
                t.substring(0, if (lastNewline > -1) t.lastIndexOf("}}") else 0)
            }
            .orEmpty()
    }

    @JvmStatic
    fun removeStartAndEndOfTemplate(text: String): String? {
        if (text.length < 2) {
            return null
        }
        val startOfTemplate = text.indexOf('|') + 1
        val endOfTemplate = text.indexOf("}}")
        if (endOfTemplate >= 0) {
            return text.substring(startOfTemplate, endOfTemplate).trim()
        }
        log.error("Could not remove start and end of template.")
        return null
    }

    @JvmStatic
    fun splitInfoboxByParameter(infoboxTemplatePartOfArticle: String?): MutableMap<String, String?> {
        return splitByParameter(infoboxTemplatePartOfArticle, REGEX_PARAMETER_INFOBOX_LINE)
    }

    @JvmStatic
    fun splitLootByParameter(lootTemplatePartOfArticle: String?): MutableMap<String, String?> {
        return splitByParameter(lootTemplatePartOfArticle, REGEX_PARAMETER_LOOT_LINE)
    }

    @JvmStatic
    fun splitByParameter(infoboxTemplatePartOfArticle: String?, regex: String): MutableMap<String, String?> {
        if (infoboxTemplatePartOfArticle.isNullOrEmpty()) {
            return HashMap()
        }

        val keyValuePair = HashMap<String, String?>()
        val keys = mutableListOf<String>()
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(infoboxTemplatePartOfArticle)
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                keys.add(matcher.group(1))
            }
        }

        val values = pattern.split(infoboxTemplatePartOfArticle).toList()
        val sanitizedValues = values
            .filter { it.isNotEmpty() }
            .map { it.trim() }
            .map { it.replace(Regex("\n$"), "") }
            .map { if (it.isEmpty()) null else it }

        if (keys.size != sanitizedValues.size && log.isErrorEnabled) {
            val endLength = minOf(infoboxTemplatePartOfArticle.length, 200)
            log.error(
                "Amount of keys and values don't match for article starting with: {}",
                infoboxTemplatePartOfArticle.substring(0, endLength).replace("\n", "")
            )
            return HashMap()
        }

        for (i in keys.indices) {
            keyValuePair[keys[i]] = sanitizedValues[i]
        }

        return keyValuePair
    }

    @JvmStatic
    fun splitByCommaAndTrim(input: String?): List<String> {
        return sequenceOf(input)
            .filterNotNull()
            .filter { it.trim().isNotEmpty() }
            .flatMap { it.split(",").asSequence().map(String::trim) }
            .toList()
    }

    @JvmStatic
    fun extractLowerLevels(infoboxTemplatePartOfArticleSanitized: String?): Optional<Map<String, String>> {
        if (infoboxTemplatePartOfArticleSanitized.isNullOrEmpty()) {
            return Optional.empty()
        }

        val keyValuePair = HashMap<String, String>()
        val pattern = Pattern.compile(REGEX_PARAMETER_LOWER_LEVELS, Pattern.DOTALL)
        val matcher = pattern.matcher(infoboxTemplatePartOfArticleSanitized)
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                keyValuePair[LOWER_LEVELS] = matcher.group(1)
            }
        }

        return if (keyValuePair.isEmpty()) {
            Optional.empty()
        } else {
            Optional.of(keyValuePair)
        }
    }

    @JvmStatic
    fun removeLowerLevels(infoboxTemplatePartOfArticleSanitized: String?): String {
        return infoboxTemplatePartOfArticleSanitized
            ?.let { Pattern.compile(REGEX_PARAMETER_LOWER_LEVELS_REMOVE, Pattern.DOTALL).matcher(it) }
            ?.replaceAll("")
            .orEmpty()
    }

    private fun getStartingAndEndingCurlyBrackets(text: String, start: String): Tuple2<Int, Int>? {
        val startingCurlyBrackets = text.indexOf(start)
        if (startingCurlyBrackets < 0) {
            return null
        }

        var endingCurlyBrackets = 0
        var openBracketsCounter = 0
        for (i in startingCurlyBrackets until text.length) {
            val currentChar = text[i]
            if (currentChar == '{') {
                openBracketsCounter++
            }
            if (currentChar == '}') {
                openBracketsCounter--
            }
            if (openBracketsCounter == 0) {
                endingCurlyBrackets = i + 1
                break
            }
        }
        return Tuple.of(startingCurlyBrackets, endingCurlyBrackets)
    }
}
