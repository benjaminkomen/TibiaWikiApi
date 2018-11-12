package com.tibiawiki.process;

import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.domain.objects.validation.ValidationResult;
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

    @Autowired
    private RetrieveAchievements retrieveAchievements;

    private ModifyAchievement() {
        // nothing to do, all dependencies are injected
    }

    public Try<Achievement> modify(Achievement achievement) {
        return validate(achievement)
//                .map(a -> retrieveAchievements.getAchievementJSON(a.getName()))
                ;
    }

    private Try<Achievement> validate(Achievement achievement) {
        final List<ValidationResult> validationResults = achievement.validate();

        return validationResults.isEmpty()
                ? Try.success(achievement)
                : Try.failure(ValidationException.fromResults(validationResults));
    }
}
