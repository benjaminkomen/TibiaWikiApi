package com.tibiawiki.process;

import com.tibiawiki.domain.objects.Creature;
import com.tibiawiki.domain.objects.TibiaWikiBot;
import com.tibiawiki.domain.repositories.WikiArticleRepository;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
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
        TibiaWikiBot tibiaWikiBot = new TibiaWikiBot();
//        tibiaWikiBot.login();
        wikiArticleRepository = new WikiArticleRepository(tibiaWikiBot);
    }

    public List<Creature> getCreatures() {
        CategoryMembersSimple pagesInCreaturesCategory = wikiArticleRepository.getMembersFromCategory(CATEGORY_CREATURES);
        CategoryMembersSimple pagesInListsCategory = wikiArticleRepository.getMembersFromCategory(CATEGORY_LISTS);

        List<String> creaturesCategory = new ArrayList<>();
        for (String pageName : pagesInCreaturesCategory) {
            creaturesCategory.add(pageName);
        }

        List<String> listsCategory = new ArrayList<>();
        for (String pageName : pagesInListsCategory) {
            listsCategory.add(pageName);
        }

        List<String> pagesInCreaturesCategoryButNotLists = creaturesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return obtainCreaturesInBulk(pagesInCreaturesCategoryButNotLists);
//        return obtainCreaturesOneByOne(pagesInCreaturesCategoryButNotLists);
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
