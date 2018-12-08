package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.factories.WikiObjectFactory;
import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.domain.objects.validation.ValidationResult;
import com.tibiawiki.domain.repositories.ArticleRepository;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 1. Validate modified Achievement
 * 2. Get current wikipage
 * 3. Replace infobox part of template with newly made infobox part of template
 * 4. Edit the wiki (via a repository)
 */
@Component
public class ModifyAchievement {

    private WikiObjectFactory wikiObjectFactory;
    private JsonFactory jsonFactory;
    private ArticleFactory articleFactory;
    private ArticleRepository articleRepository;

    @Autowired
    private ModifyAchievement(WikiObjectFactory wikiObjectFactory, JsonFactory jsonFactory, ArticleFactory articleFactory,
                              ArticleRepository articleRepository) {
        this.wikiObjectFactory = wikiObjectFactory;
        this.jsonFactory = jsonFactory;
        this.articleFactory = articleFactory;
        this.articleRepository = articleRepository;
    }

    public Try<Achievement> modify(Achievement achievement, String editSummary) {
        final String originalAchievement = articleRepository.getArticle(achievement.getName());

        return validate(achievement)
                .map(a -> wikiObjectFactory.createJSONObject(a, "Achievement"))
                .map(json -> jsonFactory.convertJsonToInfoboxPartOfArticle(json, achievement.fieldOrder()))
                .map(s -> articleFactory.insertInfoboxPartOfArticle(originalAchievement, s))
                .map(s -> articleRepository.modifyArticle(achievement.getName(), s, editSummary))
                .flatMap(b -> b
                        ? Try.success(achievement)
                        : Try.failure(new ValidationException("Unable to edit achievement."))
                );
    }

    private Try<Achievement> validate(Achievement achievement) {
        final List<ValidationResult> validationResults = achievement.validate();

        return validationResults.isEmpty()
                ? Try.success(achievement)
                : Try.failure(ValidationException.fromResults(validationResults));
    }
}
