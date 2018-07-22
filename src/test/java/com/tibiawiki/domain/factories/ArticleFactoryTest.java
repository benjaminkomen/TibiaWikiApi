package com.tibiawiki.domain.factories;

import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class ArticleFactoryTest {

    private static final String SOME_TITLE = "Goo Goo Dancer";
    private static final String SOME_TEXT_ONLY_INFOBOX = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "}}";
    private static final String SOME_TEXT_NO_INFOBOX = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. {{}}}}}{";
    private static final String SOME_TEXT_EMPTY = "";

    private ArticleFactory target;
    private Article article;
    private SimpleArticle simpleArticle;
    @Mock
    private WikiBot wikiBot;


    @BeforeEach
    public void setup() {
        target = new ArticleFactory();
        wikiBot = mock(WikiBot.class);
    }

    @Test
    public void testExtractInfoboxPartOfArticle_EmptyText() {
        simpleArticle = makeSimpleArticle(SOME_TITLE, SOME_TEXT_EMPTY);
        article = new Article(wikiBot, simpleArticle);

        Mockito.doReturn(simpleArticle).when(wikiBot).readData(any());

        String result = target.extractInfoboxPartOfArticle(article);

        assertThat(result, is(""));
    }

    @Test
    public void testExtractInfoboxPartOfArticle_NoInfobox() {
        simpleArticle = makeSimpleArticle(SOME_TITLE, SOME_TEXT_NO_INFOBOX);
        article = new Article(wikiBot, simpleArticle);

        Mockito.doReturn(simpleArticle).when(wikiBot).readData(any());

        String result = target.extractInfoboxPartOfArticle(article);

        assertThat(result, is(""));
    }

    @Test
    public void testExtractInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        simpleArticle = makeSimpleArticle(SOME_TITLE, SOME_TEXT_ONLY_INFOBOX);
        article = new Article(wikiBot, simpleArticle);

        Mockito.doReturn(simpleArticle).when(wikiBot).readData(any());

        String result = target.extractInfoboxPartOfArticle(article);

        assertThat(result, is(SOME_TEXT_ONLY_INFOBOX));
    }

    private SimpleArticle makeSimpleArticle(String title, String text) {
        SimpleArticle simpleArticle = new SimpleArticle(title);
        simpleArticle.setText(text);
        return simpleArticle;
    }

}