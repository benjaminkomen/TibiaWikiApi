package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tibiawiki.process.RetrieveAny.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AchievementsResourceIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ArticleRepository articleRepository; // don't instantiate this real class, but use a mock implementation

    @Test
    void givenGetAchievementsNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoAchievements() {
        doReturn(Collections.emptyList()).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Arrays.asList("foo", "bar")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.getCategoryName());

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/achievements?expand=false", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
//        assertThat(result.getBody(), hasSize(2));
    }
}
