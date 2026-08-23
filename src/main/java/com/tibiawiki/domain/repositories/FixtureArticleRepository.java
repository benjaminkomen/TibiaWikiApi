package com.tibiawiki.domain.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fastily.jwiki.core.NS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Offline {@link ArticleRepository} used by the {@code fixtures} Spring profile.
 * Loads recorded category lists and article wikitext from disk so bootRun / CI
 * never call Fandom or tibiawiki.dev.
 */
@Repository
@Profile("fixtures")
public class FixtureArticleRepository implements ArticleRepository {

    private static final Logger LOG = LoggerFactory.getLogger(FixtureArticleRepository.class);
    private static final TypeReference<Map<String, List<String>>> CATEGORIES_TYPE =
            new TypeReference<Map<String, List<String>>>() {
            };

    private final Map<String, List<String>> categories;
    private final Map<String, String> articles;

    public FixtureArticleRepository(
            @Value("${wiki.fixtures.path:regression/fixtures}") String fixturesPath) {
        Path dir = resolveFixturesDir(fixturesPath);
        this.categories = readCategories(dir.resolve("categories.json"));
        this.articles = readArticles(dir.resolve("articles"));
        LOG.info("Loaded {} categories and {} articles from {} (no outbound wiki HTTP)",
                categories.size(), articles.size(), dir.toAbsolutePath());
    }

    @Override
    public List<String> getPageNamesFromCategory(String categoryName) {
        return List.copyOf(categories.getOrDefault(categoryName, List.of()));
    }

    @Override
    public List<String> getPageNamesFromCategory(String categoryName, NS namespace) {
        return getPageNamesFromCategory(categoryName);
    }

    @Override
    public Map<String, String> getArticlesFromCategory(List<String> pageNames) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String pageName : pageNames) {
            String text = lookupArticle(pageName);
            if (text != null) {
                result.put(pageName, text);
            }
        }
        return result;
    }

    @Override
    public Map<String, String> getArticlesFromCategory(String categoryName) {
        return getArticlesFromCategory(getPageNamesFromCategory(categoryName));
    }

    @Override
    public List<String> getPageNamesUsingTemplate(String templateName) {
        return List.of();
    }

    @Override
    public String getArticle(String pageName) {
        return lookupArticle(pageName);
    }

    @Override
    public boolean modifyArticle(String pageName, String pageContent, String editSummary) {
        return false;
    }

    private String lookupArticle(String pageName) {
        if (pageName == null) {
            return null;
        }
        String spaced = pageName.replace('_', ' ');
        String underscored = pageName.replace(' ', '_');
        if (articles.containsKey(pageName)) {
            return articles.get(pageName);
        }
        if (articles.containsKey(spaced)) {
            return articles.get(spaced);
        }
        return articles.get(underscored);
    }

    private static Map<String, List<String>> readCategories(Path file) {
        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException("Missing fixture file: " + file.toAbsolutePath());
        }
        try {
            return new ObjectMapper().readValue(file.toFile(), CATEGORIES_TYPE);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read " + file.toAbsolutePath(), e);
        }
    }

    private static Map<String, String> readArticles(Path articlesDir) {
        if (!Files.isDirectory(articlesDir)) {
            throw new IllegalStateException("Missing fixture articles dir: " + articlesDir.toAbsolutePath());
        }
        Map<String, String> loaded = new LinkedHashMap<>();
        try (Stream<Path> paths = Files.list(articlesDir)) {
            paths.filter(path -> path.getFileName().toString().endsWith(".wiki"))
                    .sorted()
                    .forEach(path -> {
                        String filename = path.getFileName().toString();
                        String title = filename.substring(0, filename.length() - ".wiki".length());
                        try {
                            loaded.put(title, Files.readString(path));
                        } catch (IOException e) {
                            throw new IllegalStateException("Unable to read " + path, e);
                        }
                    });
        } catch (IOException e) {
            throw new IllegalStateException("Unable to list " + articlesDir, e);
        }
        return Collections.unmodifiableMap(loaded);
    }

    static Path resolveFixturesDir(String configured) {
        Path given = Path.of(configured);
        if (isFixturesDir(given)) {
            return given.toAbsolutePath().normalize();
        }
        Path fromUserDir = Path.of(System.getProperty("user.dir"), configured);
        if (isFixturesDir(fromUserDir)) {
            return fromUserDir.toAbsolutePath().normalize();
        }
        Path cursor = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        for (int i = 0; i < 6 && cursor != null; i++) {
            Path candidate = cursor.resolve("regression/fixtures");
            if (isFixturesDir(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
            cursor = cursor.getParent();
        }
        throw new IllegalStateException(
                "Could not find wiki fixtures at '" + configured + "'. "
                        + "Set wiki.fixtures.path or WIKI_FIXTURES_PATH to the directory that contains categories.json.");
    }

    private static boolean isFixturesDir(Path dir) {
        return Files.isDirectory(dir) && Files.isRegularFile(dir.resolve("categories.json"));
    }
}
