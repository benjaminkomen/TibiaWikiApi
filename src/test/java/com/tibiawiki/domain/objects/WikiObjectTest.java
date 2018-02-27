package com.tibiawiki.domain.objects;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class WikiObjectTest {

    private WikiObject target;

    @Before
    public void setup() {

    }

    @Ignore
    @Test
    public void testGetFields_WikiObject() {
        target = makeWikiObject();

        List<String> result = target.getFieldNames();

        assertThat(result, hasSize(3));
    }


    private WikiObject makeWikiObject() {
        WikiObject wikiObject = new WikiObject.WikiObjectImpl();
//        wikiObject.setActualname("bear");
//        wikiObject.setArticle(Article.A);
//        wikiObject.setStatus(Status.ACTIVE);
        return wikiObject;
    }
}
