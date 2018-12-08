package com.tibiawiki.domain.utils;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateUtils {

    private static final Logger log = LoggerFactory.getLogger(TemplateUtils.class);
    private static final String REGEX_PARAMETER_NEW = "\\|\\s+?([A-Za-z0-9_\\-]+)\\s*?=";
    private static final String REGEX_PARAMETER_LOWER_LEVELS = "\\|\\s+?lowerlevels\\s*?=((?:.*?\\{\\{.*?}})+)";
    private static final String REGEX_PARAMETER_LOWER_LEVELS_REMOVE = "\\|\\s+?lowerlevels\\s*?=((.*?\\{\\{.*?}})+)";
    private static final String LOWER_LEVELS = "lowerlevels";

    private TemplateUtils() {
        // no-args constructor, only static methods
    }

    public static String getBetweenOuterBalancedBrackets(String text, String start) {
        final Tuple2<Integer, Integer> startingAndEndingCurlyBrackets = getStartingAndEndingCurlyBrackets(text, start);
        return text.substring(startingAndEndingCurlyBrackets._1(), startingAndEndingCurlyBrackets._2());
    }

    /**
     * @param text  to search in
     * @param start string which denoted the start of the balanced brackets
     * @return two strings, the first is the substring of the provided text before the start of the balanced brackets,
     * the second is the substring after the start of the balanced brackets.
     */
    public static Tuple2<String, String> getBeforeAndAfterOuterBalancedBrackets(String text, String start) {
        final Tuple2<Integer, Integer> startingAndEndingCurlyBrackets = getStartingAndEndingCurlyBrackets(text, start);
        return Tuple.of(text.substring(0, startingAndEndingCurlyBrackets._1()), text.substring(startingAndEndingCurlyBrackets._2()));
    }

    /**
     * Remove the first line of the input string, that is, between the start of the string and the first occurrence
     * of a \n character.
     * Remove the last line of the input string, that is, everything after the last occurrence of two }} characters. The
     * reason we are not looking for the last \n character is that some templates may end with | notes =\n}} and then
     * removing the last \n character also removes the value of the key "notes".
     */
    @NotNull
    public static String removeFirstAndLastLine(@Nullable String text) {
        return Optional.ofNullable(text)
                .map(t -> t.substring(t.indexOf('\n') + 1)) // remove first line
                .map(t -> t.substring(0, t.lastIndexOf('\n') > -1 ? t.lastIndexOf("}}") : 0)) // remove last line
                .orElse("");
    }

    public static String removeStartAndEndOfTemplate(String text) {
        if (text.length() < 2) {
            return null;
        }
        int startOfTemplate = text.indexOf('|') + 1;
        int endOfTemplate = text.indexOf("}}");
        if (startOfTemplate >= 0 && endOfTemplate >= 0) {
            return text.substring(startOfTemplate, endOfTemplate).trim();
        }
        log.error("Could not remove start and end of template.");
        return null;
    }

    @NotNull
    public static Map<String, String> splitByParameter(@Nullable String infoboxTemplatePartOfArticle) {
        if (infoboxTemplatePartOfArticle == null || "".equals(infoboxTemplatePartOfArticle)) {
            return new HashMap<>();
        }

        final Map<String, String> keyValuePair = new HashMap<>();

        // first get keys
        List<String> keys = new LinkedList<>();
        Pattern pattern = Pattern.compile(REGEX_PARAMETER_NEW);
        Matcher matcher = pattern.matcher(infoboxTemplatePartOfArticle);
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                String key = matcher.group(1);
                keys.add(key);
            }
        }

        final List<String> values = Arrays.asList((pattern).split(infoboxTemplatePartOfArticle));

        // sanitize values to get rid of empty Strings
        List<String> sanitizedValues = values.stream()
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .map(s -> s.replaceAll("\n$", ""))
                .map(s -> "".equals(s) ? null : s) // transform empty strings to null values
                .collect(Collectors.toList());

        if (keys.size() != sanitizedValues.size()) {
            if (log.isErrorEnabled()) {
                int endLength = infoboxTemplatePartOfArticle.length() >= 200 ? 200 : infoboxTemplatePartOfArticle.length();
                log.error("Amount of keys and values don't match for article starting with: {}",
                        infoboxTemplatePartOfArticle.substring(0, endLength).replaceAll("\\n", ""));
                return new HashMap<>();
            }
        }

        // put keys and values into map
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = sanitizedValues.get(i);
            keyValuePair.put(key, value);
        }

        return keyValuePair;
    }

    @NotNull
    public static List<String> splitByCommaAndTrim(@Nullable String input) {
        return Stream.of(input)
                .filter(Objects::nonNull)
                .filter(i -> i.trim().length() > 0)
                .map(i -> i.split(","))
                .flatMap(lst -> Arrays.stream(lst).map(String::trim))
                .collect(Collectors.toList());
    }

    @NotNull
    public static Optional<Map<String, String>> extractLowerLevels(@Nullable String infoboxTemplatePartOfArticleSanitized) {
        if (infoboxTemplatePartOfArticleSanitized == null || "".equals(infoboxTemplatePartOfArticleSanitized)) {
            return Optional.empty();
        }

        final Map<String, String> keyValuePair = new HashMap<>();

        Pattern pattern = Pattern.compile(REGEX_PARAMETER_LOWER_LEVELS, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(infoboxTemplatePartOfArticleSanitized);
        while (matcher.find()) {
            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                String value = matcher.group(1);
                keyValuePair.put(LOWER_LEVELS, value);
            }
        }

        return keyValuePair.isEmpty()
                ? Optional.empty()
                : Optional.of(keyValuePair);
    }

    @NotNull
    public static String removeLowerLevels(@Nullable String infoboxTemplatePartOfArticleSanitized) {
        return Optional.ofNullable(infoboxTemplatePartOfArticleSanitized)
                .map(s -> Pattern.compile(REGEX_PARAMETER_LOWER_LEVELS_REMOVE, Pattern.DOTALL).matcher(s))
                .map(m -> m.replaceAll(""))
                .orElse("");
    }

    /**
     * @param text the text to search in
     * @param start the start String which denotes the start of the curly brackets
     * @return a tuple of two integers: the index of the start of the curly brackets and an index of the end of
     * the curly brackets
     */
    private static Tuple2<Integer, Integer> getStartingAndEndingCurlyBrackets(String text, String start) {
        final int startingCurlyBrackets = text.indexOf(start);

        if (startingCurlyBrackets < 0) {
            throw new IllegalArgumentException("Provided arguments text and start are not valid.");
        }

        int endingCurlyBrackets = 0;
        int openBracketsCounter = 0;
        char currentChar;

        for (int i = startingCurlyBrackets; i < text.length(); i++) {
            currentChar = text.charAt(i);
            if ('{' == currentChar) {
                openBracketsCounter++;
            }

            if ('}' == currentChar) {
                openBracketsCounter--;
            }

            if (openBracketsCounter == 0) {
                endingCurlyBrackets = i + 1;
                break;
            }
        }
        return Tuple.of(startingCurlyBrackets, endingCurlyBrackets);
    }
}