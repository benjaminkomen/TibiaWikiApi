package com.tibiawiki.domain.factories

import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.domain.objects.Book
import com.tibiawiki.domain.objects.Building
import com.tibiawiki.domain.objects.Charm
import com.tibiawiki.domain.objects.Corpse
import com.tibiawiki.domain.objects.Creature
import com.tibiawiki.domain.objects.Effect
import com.tibiawiki.domain.objects.HuntingPlace
import com.tibiawiki.domain.objects.Key
import com.tibiawiki.domain.objects.Location
import com.tibiawiki.domain.objects.Missile
import com.tibiawiki.domain.objects.Mount
import com.tibiawiki.domain.objects.NPC
import com.tibiawiki.domain.objects.Outfit
import com.tibiawiki.domain.objects.Quest
import com.tibiawiki.domain.objects.Spell
import com.tibiawiki.domain.objects.Street
import com.tibiawiki.domain.objects.TibiaObject
import com.tibiawiki.domain.objects.WikiObject
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.util.stream.Stream

/**
 * Create a WikiObject from a previously constructed JSONObject, and back.
 */
@Component
class WikiObjectFactory(
    private val objectMapper: ObjectMapper
) {

    fun createWikiObjects(jsonObjects: List<JSONObject>): Stream<WikiObject> {
        return createWikiObjects(jsonObjects.toTypedArray())
    }

    fun createWikiObjects(jsonObjects: Array<JSONObject>): Stream<WikiObject> {
        return jsonObjects.asList().stream().map { createWikiObject(it) }
    }

    /**
     * Creates a WikiObject from a JSONObject.
     * The reverse is achieved by [createJSONObject] when saving the JSON back to the wiki.
     */
    fun createWikiObject(wikiObjectJson: JSONObject): WikiObject? {
        val templateType = try {
            wikiObjectJson.get(TEMPLATE_TYPE) as String
        } catch (_: JSONException) {
            log.error("WikiObjectJson does not contain any templateType.")
            return WikiObject.WikiObjectImpl()
        }

        return when (templateType) {
            TEMPLATE_TYPE_ACHIEVEMENT -> mapJsonToObject(wikiObjectJson, Achievement::class.java)
            TEMPLATE_TYPE_BOOK -> mapJsonToObject(wikiObjectJson, Book::class.java)
            TEMPLATE_TYPE_BUILDING -> mapJsonToObject(wikiObjectJson, Building::class.java)
            TEMPLATE_TYPE_CHARM -> mapJsonToObject(wikiObjectJson, Charm::class.java)
            TEMPLATE_TYPE_CORPSE -> mapJsonToObject(wikiObjectJson, Corpse::class.java)
            TEMPLATE_TYPE_CREATURE -> mapJsonToObject(wikiObjectJson, Creature::class.java)
            TEMPLATE_TYPE_EFFECT -> mapJsonToObject(wikiObjectJson, Effect::class.java)
            TEMPLATE_TYPE_LOCATION -> mapJsonToObject(wikiObjectJson, Location::class.java)
            TEMPLATE_TYPE_HUNTING_PLACE -> mapJsonToObject(wikiObjectJson, HuntingPlace::class.java)
            TEMPLATE_TYPE_ITEM, TEMPLATE_TYPE_OBJECT -> mapJsonToObject(wikiObjectJson, TibiaObject::class.java)
            TEMPLATE_TYPE_KEY -> mapJsonToObject(wikiObjectJson, Key::class.java)
            TEMPLATE_TYPE_MISSILE -> mapJsonToObject(wikiObjectJson, Missile::class.java)
            TEMPLATE_TYPE_MOUNT -> mapJsonToObject(wikiObjectJson, Mount::class.java)
            TEMPLATE_TYPE_NPC -> mapJsonToObject(wikiObjectJson, NPC::class.java)
            TEMPLATE_TYPE_OUTFIT -> mapJsonToObject(wikiObjectJson, Outfit::class.java)
            TEMPLATE_TYPE_QUEST -> mapJsonToObject(wikiObjectJson, Quest::class.java)
            TEMPLATE_TYPE_SPELL -> mapJsonToObject(wikiObjectJson, Spell::class.java)
            TEMPLATE_TYPE_STREET -> mapJsonToObject(wikiObjectJson, Street::class.java)
            else -> {
                log.warn("object type '{}' not supported, terminating..", templateType)
                null
            }
        }
    }

    /**
     * Creates an Article from a WikiObject, for saving to the wiki.
     * The reverse is achieved by [createWikiObject] when reading from the wiki.
     */
    @Suppress("UNCHECKED_CAST")
    fun createJSONObject(wikiObject: WikiObject, templateType: String): JSONObject {
        val wikiObjectAsMap = objectMapper.convertValue(wikiObject, Map::class.java) as MutableMap<String, Any>
        wikiObjectAsMap[TEMPLATE_TYPE] = templateType
        return JSONObject(wikiObjectAsMap)
    }

    private fun <T> mapJsonToObject(wikiObjectJson: JSONObject, clazz: Class<T>): T? {
        return try {
            objectMapper.readValue(wikiObjectJson.toString(), clazz)
        } catch (e: RuntimeException) {
            log.error("Unable to convert json to {} object.", clazz.toString(), e)
            null
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WikiObjectFactory::class.java)
        private const val TEMPLATE_TYPE = "templateType"
        private const val TEMPLATE_TYPE_ACHIEVEMENT = "Achievement"
        private const val TEMPLATE_TYPE_BOOK = "Book"
        private const val TEMPLATE_TYPE_BUILDING = "Building"
        private const val TEMPLATE_TYPE_CHARM = "Charm"
        private const val TEMPLATE_TYPE_CORPSE = "Corpse"
        private const val TEMPLATE_TYPE_CREATURE = "Creature"
        private const val TEMPLATE_TYPE_EFFECT = "Effect"
        private const val TEMPLATE_TYPE_LOCATION = "Geography"
        private const val TEMPLATE_TYPE_HUNTING_PLACE = "Hunt"
        private const val TEMPLATE_TYPE_MOUNT = "Mount"
        private const val TEMPLATE_TYPE_ITEM = "Item"
        private const val TEMPLATE_TYPE_KEY = "Key"
        private const val TEMPLATE_TYPE_MISSILE = "Missile"
        private const val TEMPLATE_TYPE_NPC = "NPC"
        private const val TEMPLATE_TYPE_OBJECT = "Object"
        private const val TEMPLATE_TYPE_OUTFIT = "Outfit"
        private const val TEMPLATE_TYPE_QUEST = "Quest"
        private const val TEMPLATE_TYPE_SPELL = "Spell"
        private const val TEMPLATE_TYPE_STREET = "Street"
    }
}
