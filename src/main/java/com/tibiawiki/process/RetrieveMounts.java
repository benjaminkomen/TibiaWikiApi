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
public class RetrieveMounts extends RetrieveAnyService {

    @Autowired
    public RetrieveMounts(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getMountsList() {
        final List<String> mountsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.MOUNT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return mountsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getMountsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getMountsList());
    }

    public JSONObject getMountJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
