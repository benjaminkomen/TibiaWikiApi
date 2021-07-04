package com.tibiawiki.process;

import com.tibiawiki.domain.RetrieveAnyService;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.mediawiki.ArticleRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RetrieveOutfits extends RetrieveAnyService {

    @Autowired
    public RetrieveOutfits(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getOutfitsList() {
        final List<String> outfitsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.OUTFIT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return outfitsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getOutfitsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getOutfitsList());
    }

    public JSONObject getOutfitJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
