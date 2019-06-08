package com.tibiawiki.process;

import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.factories.WikiObjectFactory;
import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.repositories.ArticleRepository;
import io.vavr.control.Try;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ModifyAnyTest {

    private static final JSONObject SOME_JSON_OBJECT = new JSONObject();

    private ModifyAny target;
    @Mock
    private WikiObjectFactory wikiObjectFactory;
    @Mock
    private JsonFactory jsonFactory;
    @Mock
    private ArticleFactory articleFactory;
    @Mock
    private ArticleRepository articleRepository;

    @BeforeEach
    public void setup() {
        wikiObjectFactory = mock(WikiObjectFactory.class);
        jsonFactory = mock(JsonFactory.class);
        articleFactory = mock(ArticleFactory.class);
        articleRepository = mock(ArticleRepository.class);
        target = new ModifyAny(wikiObjectFactory, jsonFactory, articleFactory, articleRepository);
    }

    @Test
    public void testModify_Success() {
        WikiObject someAchievement = makeAchievement();
        doReturn("").when(articleRepository).getArticle(anyString());
        doReturn(SOME_JSON_OBJECT).when(wikiObjectFactory).createJSONObject(eq(someAchievement), anyString());
        doReturn("").when(jsonFactory).convertJsonToInfoboxPartOfArticle(any(JSONObject.class), any(List.class));
        doReturn("").when(articleFactory).insertInfoboxPartOfArticle(anyString(), anyString());
        doReturn(true).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        Try<WikiObject> result = target.modify(someAchievement, "[test] editing the page");

        assertThat("Test: successfully modified article", result.isSuccess());
    }

    @Test
    public void testModify_Failure() {
        WikiObject someAchievement = makeAchievement();
        doReturn("").when(articleRepository).getArticle(anyString());
        doReturn(SOME_JSON_OBJECT).when(wikiObjectFactory).createJSONObject(eq(someAchievement), anyString());
        doReturn("").when(jsonFactory).convertJsonToInfoboxPartOfArticle(any(JSONObject.class), any(List.class));
        doReturn("").when(articleFactory).insertInfoboxPartOfArticle(anyString(), anyString());
        doReturn(false).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        Try<WikiObject> result = target.modify(someAchievement, "[test] editing the page");

        assertThat("Test: failed to modify article", result.isFailure());
    }

    private WikiObject makeAchievement() {
        return Achievement.builder()
                .grade(1)
                .name("Goo Goo Dancer")
                .description("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!")
                .spoiler("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.")
                .premium(YesNo.YES_LOWERCASE)
                .points(1)
                .secret(YesNo.YES_LOWERCASE)
                .implemented("9.6")
                .achievementid(319)
                .relatedpages("[[Muck Remover]], [[Mucus Plug]]")
                .build();
    }


}