package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.QuestType;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor
@Component
public class Quest extends WikiObject {

    private String aka;
    private String reward;
    private String location;
    private YesNo rookgaardquest;
    private QuestType type;
    private Integer lvl;
    private Integer lvlrec;
    private String lvlnote;
    private YesNo log;
    private String time;
    private String timealloc;
    private YesNo premium;
    private YesNo transcripts;
    private String dangers;
    private String legend;

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "aka", "reward", "location", "rookgaardquest", "type", "lvl", "lvlrec", "lvlnote",
                "log", "time", "timealloc", "premium", "transcripts", "dangers", "legend", "history", "status");
    }
}