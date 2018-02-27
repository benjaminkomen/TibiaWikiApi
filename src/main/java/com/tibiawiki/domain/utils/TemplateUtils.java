package com.tibiawiki.domain.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateUtils {

    private static final Logger log = LoggerFactory.getLogger(TemplateUtils.class);
    public static final String REGEX_PARAMETER_NEW = "\\|\\s+?([A-Za-z0-9_]+)\\s*?=";

    private TemplateUtils() {}

    public static String getBetweenOuterBalancedBrackets(String text, String start) {
        int startingCurlyBrackets = text.indexOf(start);
        assert (startingCurlyBrackets >=0) : "text: " + text + " start: " + start;
        int endingCurlyBrackets = 0;
        int openBracketsCounter = 0;
        char currentChar;

        for (int i=startingCurlyBrackets; i < text.length(); i++) {
            currentChar = text.charAt(i);
            if ('{' == currentChar) {
                openBracketsCounter++;
            }

            if ('}' == currentChar) {
                openBracketsCounter--;
            }

            if (openBracketsCounter == 0) {
                endingCurlyBrackets = i+1;
                break;
            }
        }
        return text.substring(startingCurlyBrackets, endingCurlyBrackets);
    }

    public static String removeFirstAndLastLine(String text) {
        String firstLineRemoved = text.substring(text.indexOf('\n')+1);
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
        for (int i=0; i < keys.size(); i++) {
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
}