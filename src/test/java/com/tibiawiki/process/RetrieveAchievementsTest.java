package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tibiawiki.process.RetrieveAchievements.CATEGORY_ACHIEVEMENTS;
import static com.tibiawiki.process.RetrieveAchievements.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

@Ignore
public class RetrieveAchievementsTest {

    private RetrieveAchievements target;

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleFactory articleFactory;
    @Mock
    private JsonFactory jsonFactory;

//    @BeforeEach
//    public void setup() {
//        target = new RetrieveAchievements();
//        articleRepository = mock(ArticleRepository.class);
//        articleFactory = mock(ArticleFactory.class);
//        jsonFactory = mock(JsonFactory.class);
//
//        CategoryMembersSimple achievements = null;
//        CategoryMembersSimple lists = null;
//
//        when(articleRepository.getMembersFromCategory(CATEGORY_ACHIEVEMENTS)).thenReturn(achievements);
//        when(articleRepository.getMembersFromCategory(CATEGORY_LISTS)).thenReturn(lists);
//    }
//
//    @Test
//    public void testGetAchievementsJSON() {
//        final Stream<JSONObject> result = target.getAchievementsJSON();
//
//        assertThat(result.collect(Collectors.toList()), hasSize(0));
//    }

}