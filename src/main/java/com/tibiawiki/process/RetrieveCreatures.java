package com.tibiawiki.process;

import com.tibiawiki.domain.objects.Creature;
import com.tibiawiki.domain.objects.TibiaWikiBot;
import com.tibiawiki.domain.repositories.WikiArticleRepository;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveCreatures {

    private static final String CATEGORY_LISTS = "Lists";
    private static final String CATEGORY_CREATURES = "Creatures";

    private WikiArticleRepository wikiArticleRepository;

    public RetrieveCreatures() {
        wikiArticleRepository = new WikiArticleRepository(new TibiaWikiBot());
    }

    public List<Creature> getCreatures() {
        return getCreatures(false);
    }

    public List<Creature> getCreatures(boolean oneByOne) {
        final List<String> creaturesCategory = new ArrayList<>();
        for (String pageName : wikiArticleRepository.getMembersFromCategory(CATEGORY_CREATURES)) {
            creaturesCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : wikiArticleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInCreaturesCategoryButNotLists = creaturesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainCreaturesOneByOne(pagesInCreaturesCategoryButNotLists)
                : obtainCreaturesInBulk(pagesInCreaturesCategoryButNotLists);
    }

    public Optional<Creature> getCreature(String pageName) {
        return Stream.of(wikiArticleRepository.getWikiObject(pageName))
                .filter(Creature.class::isInstance)
                .map(Creature.class::cast)
                .findAny();
    }

    private List<Creature> obtainCreaturesInBulk(List<String> pageNames) {
        return StreamEx.ofSubLists(pageNames, 250)
                .flatMap(names -> wikiArticleRepository.getWikiObjects(names).stream())
                .filter(Creature.class::isInstance)
                .map(Creature.class::cast)
                .collect(Collectors.toList());
    }

    private List<Creature> obtainCreaturesOneByOne(List<String> pageNames) {
        return pageNames.stream()
                .map(pageName -> wikiArticleRepository.getWikiObject(pageName))
                .filter(Creature.class::isInstance)
                .map(Creature.class::cast)
                .collect(Collectors.toList());
    }
}
