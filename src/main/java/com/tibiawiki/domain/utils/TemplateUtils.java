package com.tibiawiki.domain.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateUtils {

    private static final Logger log = LoggerFactory.getLogger(TemplateUtils.class);
    private static final String REGEX_PARAMETER_NEW = "\\|\\s+?([A-Za-z0-9_\\-]+)\\s*?=";
    private static final String REGEX_PARAMETER_LOWER_LEVELS = "\\|\\s+?lowerlevels\\s*?=((?:.*?\\{\\{.*?}})+)";
    private static final String REGEX_PARAMETER_LOWER_LEVELS_REMOVE = "\\|\\s+?lowerlevels\\s*?=((.*?\\{\\{.*?}})+)";
    private static final String LOWER_LEVELS = "lowerlevels";

    private TemplateUtils() {
    }

    public static String getBetweenOuterBalancedBrackets(String text, String start) {
        int startingCurlyBrackets = text.indexOf(start);

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
        return text.substring(startingCurlyBrackets, endingCurlyBrackets);
    }

    public static String removeFirstAndLastLine(String text) {
        String firstLineRemoved = text.substring(text.indexOf('\n') + 1);
        return firstLineRemoved.substring(0, firstLineRemoved.lastIndexOf("}}"));
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

    public static Map<String, String> splitByParameter(String infoboxTemplatePartOfArticle) {
        Map<String, String> keyValuePair = new HashMap<>();

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
        List<String> values = Arrays.asList((pattern).split(infoboxTemplatePartOfArticle));

        // sanitize values to get rid of empty Strings
        List<String> sanitizedValue = values.stream()
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .map(s -> s.replaceAll("\n$", ""))
                .collect(Collectors.toList());


        // put keys and values into map
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = sanitizedValue.get(i);
            keyValuePair.put(key, value);
        }

        return keyValuePair;
    }

    public static List<String> splitByCommaAndTrim(String input) {
        List<String> result = new ArrayList<>();
        String[] arrayFromSplitInput = input.split(",");

        for (String arrayElement : arrayFromSplitInput) {
            result.add(arrayElement.trim());
        }

        return result;
    }

    public static Optional<Map<String, String>> extractLowerLevels(String infoboxTemplatePartOfArticleSanitized) {
        Map<String, String> keyValuePair = new HashMap<>();

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

    public static String removeLowerLevels(String infoboxTemplatePartOfArticleSanitized) {
        final Pattern pattern = Pattern.compile(REGEX_PARAMETER_LOWER_LEVELS_REMOVE, Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(infoboxTemplatePartOfArticleSanitized);
        return matcher.replaceAll("");
    }
}