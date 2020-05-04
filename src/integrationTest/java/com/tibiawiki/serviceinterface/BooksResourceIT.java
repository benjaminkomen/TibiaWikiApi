package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.enums.BookType;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.objects.Book;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.tibiawiki.TestUtils.makeHttpHeaders;
import static com.tibiawiki.process.RetrieveAny.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksResourceIT {

    private static final String INFOBOX_BOOK_TEXT = "{{Infobox Book|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| booktype     = Book (Brown)\n" +
            "| title        = Dungeon Survival Guide\n" +
            "| pagename     = Dungeon Survival Guide (Book)\n" +
            "| location     = [[Rookgaard Academy]]\n" +
            "| blurb        = Tips for exploring dungeons, and warning against being reckless.\n" +
            "| returnpage   = Rookgaard Libraries\n" +
            "| relatedpages = [[Rope]], [[Shovel]]\n" +
            "| text         = Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills" +
            " in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter" +
            " dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a" +
            " supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups" +
            " and not alone. For more help read all the books of the academy before you begin exploring. Traveling in" +
            " the dungeons will reward the cautious and brave, but punish the reckless.\n" +
            "}}\n";
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ArticleRepository articleRepository; // don't instantiate this real class, but use a mock implementation

    @Test
    void givenGetBooksNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoBookNames() {
        doReturn(Collections.singletonList("baz")).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Arrays.asList("foo", "bar", "baz")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BOOK.getCategoryName());

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/books?expand=false", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(2));
        assertThat(result.getBody().get(0), is("foo"));
        assertThat(result.getBody().get(1), is("bar"));
    }

    @Test
    void givenGetBooksExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneBook() {
        doReturn(Collections.emptyList()).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Collections.singletonList("Dungeon Survival Guide (Book)")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BOOK.getCategoryName());
        doReturn(Map.of("Dungeon Survival Guide (Book)", INFOBOX_BOOK_TEXT)).when(articleRepository).getArticlesFromCategory(Collections.singletonList("Dungeon Survival Guide (Book)"));

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/books?expand=true", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(1));
        assertThat(((Map) result.getBody().get(0)).get("templateType"), is("Book"));
        assertThat(((Map) result.getBody().get(0)).get("booktype"), is("Book (Brown)"));
        assertThat(((Map) result.getBody().get(0)).get("title"), is("Dungeon Survival Guide"));
        assertThat(((Map) result.getBody().get(0)).get("pagename"), is("Dungeon Survival Guide (Book)"));
        assertThat(((Map) result.getBody().get(0)).get("location"), is("[[Rookgaard Academy]]"));
        assertThat(((Map) result.getBody().get(0)).get("blurb"), is("Tips for exploring dungeons, and warning against being reckless."));
        assertThat(((Map) result.getBody().get(0)).get("returnpage"), is("Rookgaard Libraries"));
        assertThat(((Map) result.getBody().get(0)).get("relatedpages"), is("[[Rope]], [[Shovel]]"));
        assertThat(((Map) result.getBody().get(0)).get("text"), is("Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups and not alone. For more help read all the books of the academy before you begin exploring. Traveling in the dungeons will reward the cautious and brave, but punish the reckless."));
    }

    @Test
    void givenGetBooksByName_whenCorrectRequest_thenResponseIsOkAndContainsTheBook() {
        doReturn(INFOBOX_BOOK_TEXT).when(articleRepository).getArticle("Dungeon Survival Guide (Book)");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/books/Dungeon Survival Guide (Book)", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        final JSONObject resultAsJSON = new JSONObject(result.getBody());
        assertThat(resultAsJSON.get("templateType"), is("Book"));
        assertThat(resultAsJSON.get("booktype"), is("Book (Brown)"));
        assertThat(resultAsJSON.get("title"), is("Dungeon Survival Guide"));
        assertThat(resultAsJSON.get("pagename"), is("Dungeon Survival Guide (Book)"));
        assertThat(resultAsJSON.get("location"), is("[[Rookgaard Academy]]"));
        assertThat(resultAsJSON.get("blurb"), is("Tips for exploring dungeons, and warning against being reckless."));
        assertThat(resultAsJSON.get("returnpage"), is("Rookgaard Libraries"));
        assertThat(resultAsJSON.get("relatedpages"), is("[[Rope]], [[Shovel]]"));
        assertThat(resultAsJSON.get("text"), is("Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups and not alone. For more help read all the books of the academy before you begin exploring. Traveling in the dungeons will reward the cautious and brave, but punish the reckless."));
    }

    @Test
    void givenGetBooksByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).when(articleRepository).getArticle("Foobar");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/books/Foobar", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void givenPutBook_whenCorrectRequest_thenResponseIsOkAndContainsTheModifiedBook() {
        final String editSummary = "[bot] editing during integration test";

        doReturn(INFOBOX_BOOK_TEXT).when(articleRepository).getArticle("Dungeon Survival Guide (Book)");
        doReturn(true).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        final HttpHeaders httpHeaders = makeHttpHeaders(editSummary);

        final ResponseEntity<Void> result = restTemplate.exchange("/api/books", HttpMethod.PUT, new HttpEntity<>(makeBook(), httpHeaders), Void.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void givenPutBook_whenCorrectRequestButUnableToEditWiki_thenResponseIsBadRequest() {
        final String editSummary = "[bot] editing during integration test";

        doReturn(INFOBOX_BOOK_TEXT).when(articleRepository).getArticle("Dungeon Survival Guide (Book)");
        doReturn(false).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        final HttpHeaders httpHeaders = makeHttpHeaders(editSummary);

        final ResponseEntity<Void> result = restTemplate.exchange("/api/books", HttpMethod.PUT, new HttpEntity<>(makeBook(), httpHeaders), Void.class);
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private Book makeBook() {
        return Book.builder()
                .booktype(BookType.BOOK_BROWN)
                .title("Dungeon Survival Guide")
                .pagename("Dungeon Survival Guide (Book)")
                .location("[[Rookgaard Academy]]")
                .blurb("Tips for exploring dungeons, and warning against being reckless.")
                .returnpage("Rookgaard Libraries")
                .relatedpages("[[Rope]], [[Shovel]]")
                .text("Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups and not alone. For more help read all the books of the academy before you begin exploring. Traveling in the dungeons will reward the cautious and brave, but punish the reckless.")
                .build();
    }
}
