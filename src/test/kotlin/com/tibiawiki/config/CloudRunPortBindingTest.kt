package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

/**
 * Cloud Run sets `PORT`. Spring must bind it; the image ENTRYPOINT must not
 * expand it through `sh` (issue #473: `$$` → PID 1 → `1PORT`).
 */
class CloudRunPortBindingTest {

    @Test
    fun applicationPropertiesBindServerPortToPortEnvWith8080Default() {
        val properties = loadApplicationProperties()
        val serverPort = requireNotNull(properties.getProperty("server.port"))
        assertThat(serverPort, `is`("\${PORT:8080}"))

        val environment = StandardEnvironment()
        environment.propertySources.addFirst(
            MapPropertySource("test-port", mapOf("PORT" to "19080"))
        )
        assertThat(environment.resolveRequiredPlaceholders(serverPort), `is`("19080"))
    }

    @Test
    fun dockerfileEntrypointIsExecFormJavaWithoutShellOrDollar() {
        val dockerfile = Files.readString(repoFile("docker", "Dockerfile"))
        val entrypoint = dockerfile.lineSequence()
            .map { it.trim() }
            .first { it.startsWith("ENTRYPOINT ") }

        assertThat(
            entrypoint,
            `is`("""ENTRYPOINT ["java", "-Dserver.address=0.0.0.0", "-jar", "/project/TibiaWikiApi.jar"]""")
        )
        assertThat(entrypoint, not(containsString("$")))
        assertThat(entrypoint, not(containsString("sh")))
        assertThat(dockerfile, containsString("ENV PORT=8080"))
        assertThat(dockerfile, containsString("USER 65532:65532"))
    }

    private fun repoFile(vararg parts: String): Path {
        var cursor: Path? = Path.of(System.getProperty("user.dir")).toAbsolutePath()
        while (cursor != null) {
            val candidate = parts.fold(cursor) { acc, part -> acc.resolve(part) }
            if (Files.isRegularFile(candidate)) {
                return candidate
            }
            cursor = cursor.parent
        }
        throw AssertionError("Could not find ${parts.joinToString("/")}")
    }

    private fun loadApplicationProperties(): Properties {
        val stream = requireNotNull(
            javaClass.classLoader.getResourceAsStream("application.properties")
        ) { "application.properties missing from test classpath" }
        return Properties().apply { stream.use { load(it) } }
    }
}
