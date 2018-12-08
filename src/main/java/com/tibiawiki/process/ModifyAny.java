package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.factories.WikiObjectFactory;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.domain.objects.validation.ValidationResult;
import com.tibiawiki.domain.repositories.ArticleRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 1. Validate modified WikiObject
 * 2. Get current wikipage
 * 3. Replace infobox part of template with newly made infobox part of template
 * 4. Edit the wiki (via a repository)
 */
@Component
public class ModifyAny {

    private WikiObjectFactory wikiObjectFactory;
    private JsonFactory jsonFactory;
    private ArticleFactory articleFactory;
    private ArticleRepository articleRepository;

    @Autowired
    private ModifyAny(WikiObjectFactory wikiObjectFactory, JsonFactory jsonFactory, ArticleFactory articleFactory,
                      ArticleRepository articleRepository) {
        this.wikiObjectFactory = wikiObjectFactory;
        this.jsonFactory = jsonFactory;
        this.articleFactory = articleFactory;
        this.articleRepository = articleRepository;
    }

    public Try<WikiObject> modify(WikiObject wikiObject, String editSummary) {
        final String originalWikiObject = articleRepository.getArticle(wikiObject.getName());

        return validate(wikiObject)
                .map(wikiObj -> wikiObjectFactory.createJSONObject(wikiObj, wikiObj.getTemplateType()))
                .map(json -> jsonFactory.convertJsonToInfoboxPartOfArticle(json, wikiObject.fieldOrder()))
                .map(s -> articleFactory.insertInfoboxPartOfArticle(originalWikiObject, s))
                .map(s -> articleRepository.modifyArticle(wikiObject.getName(), s, editSummary))
                .flatMap(b -> b
                        ? Try.success(wikiObject)
                        : Try.failure(new ValidationException("Unable to edit wikiObject."))
                );
    }

    private Try<WikiObject> validate(WikiObject wikiObject) {
        final List<ValidationResult> validationResults = wikiObject.validate();

        return validationResults.isEmpty()
                ? Try.success(wikiObject)
                : Try.failure(ValidationException.fromResults(validationResults));
    }
}
