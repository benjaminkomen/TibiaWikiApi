package com.tibiawiki.domain.factories;

import com.tibiawiki.domain.objects.Creature;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ArticleFactoryTest {

    private static final String SOME_CREATURE =
            "";

    @Ignore
    @Test
    public void testCreateArticleText() {
        Creature creature = makeCreature();
        String result = ArticleFactory.createArticleText(creature);

        assertThat(result, is(SOME_CREATURE));
    }

    private Creature makeCreature() {
        return new Creature();
    }
}