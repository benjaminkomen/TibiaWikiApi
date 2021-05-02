package com.tibiawiki.process;

import com.tibiawiki.domain.mediawiki.ArticleRepository;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.JsonFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RetrieveMounts extends RetrieveAny {

    @Autowired
    public RetrieveMounts(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getMountsList() {
        final List<String> mountsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.MOUNT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return mountsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getMountsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getMountsList());
    }

    public Optional<JSONObject> getMountJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
