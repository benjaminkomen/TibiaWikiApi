package com.tibiawiki.domain.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.WikiObject;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class WikiObjectFactoryTest {

    private static final String SOME_TEMPLATE_TYPE = "Achievement";
    private WikiObjectFactory target;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = mock(ObjectMapper.class);
        target = new WikiObjectFactory(objectMapper);
    }

    @Test
    void testCreateWikiObject_MissingTemplateType() {
        var someJSONObject = makeJSONObject();
        WikiObject result = target.createWikiObject(someJSONObject);

        assertThat(result, instanceOf(WikiObject.class));
        assertThat(result.getTemplateType(), is("WikiObjectImpl"));
    }

    @Test
    void testCreateWikiObject_Achievement() throws Exception {
        doReturn(makeAchievement()).when(objectMapper).readValue(anyString(), org.mockito.ArgumentMatchers.any(Class.class));

        var someJSONObject = makeJSONObject();
        someJSONObject.put("templateType", "Achievement");
        WikiObject result = target.createWikiObject(someJSONObject);

        assertThat(result, instanceOf(WikiObject.class));
        assertThat(result.getTemplateType(), is("Achievement"));
        assertThat(result.getName(), is("Goo Goo Dancer"));
    }

    @Test
    void testCreateJSONObject_Success() {
        var someWikiObject = makeAchievement();
        Map<String, Object> someMap = new HashMap<>();
        doReturn(someMap).when(objectMapper).convertValue(org.mockito.ArgumentMatchers.any(WikiObject.class), org.mockito.ArgumentMatchers.any(Class.class));
        JSONObject result = target.createJSONObject(someWikiObject, SOME_TEMPLATE_TYPE);

        assertThat(result, instanceOf(JSONObject.class));
        assertThat(result.get("templateType"), is(SOME_TEMPLATE_TYPE));
    }

    private JSONObject makeJSONObject() {
        return new JSONObject();
    }

    private WikiObject makeAchievement() {
        return Achievement.builder()
                .name("Goo Goo Dancer")
                .build();
    }
}