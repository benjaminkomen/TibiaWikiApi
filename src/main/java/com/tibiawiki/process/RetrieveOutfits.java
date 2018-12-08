package com.tibiawiki.process;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RetrieveOutfits extends RetrieveAny {

    @Autowired
    public RetrieveOutfits(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getOutfitsList() {
        final List<String> outfitsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.OUTFIT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return outfitsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getOutfitsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getOutfitsList());
    }

    public Optional<JSONObject> getOutfitJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
