package com.tibiawiki.config

import com.tibiawiki.adapters.rest.WikiCategory
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.tags.Tag
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.core.Ordered

/**
 * springdoc only sees [com.tibiawiki.adapters.rest.WikiCategoryController]'s
 * `/api/{category}` template, so Swagger UI groups achievements, items, etc.
 * under one "Wiki Categories" tag. Runtime routing stays on that single
 * controller; this customizer clones the template operations onto concrete
 * `/api/{path}` entries driven by [WikiCategory.entries].
 *
 * Runs before [OpenAPIConfiguration.wikiWriteOpenApiCustomizer] so expanded
 * PUTs still receive the write-token security scheme and 401/403 responses.
 */
class WikiCategoryOpenApiCustomizer : OpenApiCustomizer, Ordered {

    override fun getOrder(): Int = EXPANSION_ORDER

    override fun customise(openApi: OpenAPI) {
        val paths = openApi.paths ?: return
        val collectionTemplate = paths.entries
            .firstOrNull { (key, _) -> isGenericCollectionPath(key) }
            ?.value
            ?: return
        val byNameTemplate = paths.entries
            .firstOrNull { (key, _) -> isGenericByNamePath(key) }
            ?.value
            ?: return

        for (category in WikiCategory.entries) {
            paths.addPathItem(
                collectionPath(category),
                clonePathItem(collectionTemplate, category, hasName = false)
            )
            paths.addPathItem(
                byNamePath(category),
                clonePathItem(byNameTemplate, category, hasName = true)
            )
        }

        paths.keys.filter { isGenericCategoryPath(it) }.forEach { paths.remove(it) }

        publishCategoryTags(openApi)
        openApi.tags?.removeIf { it.name == GENERIC_TAG }
    }

    private fun publishCategoryTags(openApi: OpenAPI) {
        val existing = openApi.tags?.mapNotNull { it.name }?.toSet() ?: emptySet()
        for (category in WikiCategory.entries) {
            if (category.tag in existing) {
                continue
            }
            openApi.addTagsItem(
                Tag()
                    .name(category.tag)
                    .description("TibiaWiki ${category.tag} (`/api/${category.path}`)")
            )
        }
    }

    private fun clonePathItem(source: PathItem, category: WikiCategory, hasName: Boolean): PathItem {
        val copy = PathItem()
        copy.summary = source.summary
        copy.description = source.description
        copy.servers = source.servers
        copy.parameters = withoutCategoryParam(source.parameters)
        copy.extensions = source.extensions?.toMutableMap()
        source.readOperationsMap().forEach { (method, operation) ->
            copy.operation(method, cloneOperation(operation, category, method, hasName))
        }
        return copy
    }

    private fun cloneOperation(
        source: Operation,
        category: WikiCategory,
        method: PathItem.HttpMethod,
        hasName: Boolean
    ): Operation {
        val copy = Operation()
        copy.tags = mutableListOf(category.tag)
        copy.summary = summaryFor(method, category, hasName)
        copy.description = descriptionFor(category, source.description)
        copy.operationId = operationIdFor(source, category)
        copy.parameters = withoutCategoryParam(source.parameters)
        copy.requestBody = source.requestBody
        copy.responses = source.responses
        copy.deprecated = source.deprecated
        copy.security = source.security?.toMutableList()
        copy.callbacks = source.callbacks
        copy.servers = source.servers
        copy.externalDocs = source.externalDocs
        copy.extensions = source.extensions?.toMutableMap()
        return copy
    }

    private fun summaryFor(method: PathItem.HttpMethod, category: WikiCategory, hasName: Boolean): String {
        return when {
            method == PathItem.HttpMethod.PUT -> "Modify a ${category.tag} wiki object"
            hasName -> "Get a specific ${category.tag} entry by name"
            else -> "Get a list of ${category.tag}"
        }
    }

    private fun descriptionFor(category: WikiCategory, original: String?): String {
        val prefix = "TibiaWiki ${category.tag} (`/api/${category.path}`)."
        return if (original.isNullOrBlank()) prefix else "$prefix $original"
    }

    private fun operationIdFor(source: Operation, category: WikiCategory): String {
        val base = source.operationId?.takeIf { it.isNotBlank() } ?: "wikiCategory"
        return "${base}_${category.path}"
    }

    private fun withoutCategoryParam(parameters: List<Parameter>?): MutableList<Parameter>? {
        if (parameters == null) {
            return null
        }
        return parameters
            .filterNot { parameter ->
                parameter.name == CATEGORY_PARAM && (parameter.`in`.isNullOrBlank() || parameter.`in` == PATH_IN)
            }
            .toMutableList()
    }

    companion object {
        const val EXPANSION_ORDER = 0
        const val GENERIC_TAG = "Wiki Categories"
        const val CATEGORY_PARAM = "category"
        const val PATH_IN = "path"

        private val GENERIC_COLLECTION = Regex("""^/api/\{category(?::[^}]*)?}$""")
        private val GENERIC_BY_NAME = Regex("""^/api/\{category(?::[^}]*)?}/\{name}$""")
        private val GENERIC_ANY = Regex("""^/api/\{category(?::[^}]*)?}(?:/\{name})?$""")

        fun collectionPath(category: WikiCategory): String = "/api/${category.path}"

        fun byNamePath(category: WikiCategory): String = "/api/${category.path}/{name}"

        fun isGenericCollectionPath(path: String): Boolean = GENERIC_COLLECTION.matches(path)

        fun isGenericByNamePath(path: String): Boolean = GENERIC_BY_NAME.matches(path)

        fun isGenericCategoryPath(path: String): Boolean = GENERIC_ANY.matches(path)
    }
}
