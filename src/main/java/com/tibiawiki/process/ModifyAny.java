package com.tibiawiki.process;

import com.tibiawiki.domain.ArticleRepository;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.factories.WikiObjectFactory;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.domain.objects.validation.ValidationResult;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 1. Validate modified WikiObject
 * 2. Get current wikipage
 * 3. Replace infobox part of template with newly made infobox part of template
 * 4. Edit the wiki (via a repository)
 */
@Component
@RequiredArgsConstructor
public class ModifyAny {

    private final WikiObjectFactory wikiObjectFactory;
    private final JsonFactory jsonFactory;
    private final ArticleFactory articleFactory;
    private final ArticleRepository articleRepository;

    public Try<WikiObject> modify(WikiObject wikiObject, String editSummary) {
        final String originalWikiObject = articleRepository.getArticle(wikiObject.getName());

        return validate(wikiObject)
                .map(wikiObj -> wikiObjectFactory.createJSONObject(wikiObj, wikiObj.getTemplateType()))
                .map(json -> jsonFactory.convertJsonToInfoboxPartOfArticle(json, wikiObject.fieldOrder()))
                .map(s -> articleFactory.insertInfoboxPartOfArticle(originalWikiObject, s))
                .flatMap(s -> s.isEmpty()
                        ? Try.failure(new IllegalArgumentException("Could not find required text in article"))
                        : Try.success(s.get())
                )
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
