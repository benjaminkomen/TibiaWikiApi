package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

/**
 * Cloud Build `gcloud` `--format` projections on `status.conditions[?type=Ready]`
 * return empty (issue #477). Ready-wait must parse JSON and must not treat an
 * unparsed condition as success by itself.
 *
 * Cloud Run revisions often omit `status.url`. After Ready, smoke URL
 * resolution must fall back to the service URL so a Ready deploy cannot fail
 * with "could not read status.url for revision".
 */
class CloudRunReadyWaitTest {

    @Test
    fun releaseScriptUsesJsonDescribeNotConditionProjection() {
        val script = Files.readString(repoFile("scripts", "cloud-run-release.sh"))
        assertThat(script, not(containsString("value[separator=|]")))
        assertThat(script, containsString("--format=json"))
        assertThat(script, containsString("scripts/lib/cloud-run-ready.sh"))
        assertThat(script, containsString("evaluate_ready_wait"))
        assertThat(script, containsString("post_ready_smoke_url"))
        assertThat(script, containsString("parse_service_url_json"))
        assertThat(script, not(containsString("could not read status.url for revision")))
    }

    @Test
    fun readyParseHelperSelfTestPasses() {
        val helper = repoFile("scripts", "lib", "cloud-run-ready.sh")
        val process = ProcessBuilder("bash", helper.toString(), "self-test")
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        val finished = process.waitFor(30, TimeUnit.SECONDS)
        assertThat("self-test timed out:\n$output", finished, `is`(true))
        assertThat("self-test exit ${process.exitValue()}:\n$output", process.exitValue(), `is`(0))
        assertThat(output, containsString("ok empty_status_latest_ready_match"))
        assertThat(output, containsString("ok ready_false_despite_latest"))
        assertThat(output, containsString("ok still_starting_wait"))
        assertThat(output, containsString("ok cli_empty_ready_latest_match"))
        assertThat(output, containsString("ok cli_ready_false"))
        assertThat(output, containsString("ok cli_still_starting"))
        assertThat(output, containsString("ok parse_revision_url_missing"))
        assertThat(output, containsString("ok parse_service_url"))
        assertThat(output, containsString("ok resolve_smoke_url_service_fallback"))
        assertThat(output, containsString("ok resolve_smoke_url_other_latest_ready"))
        assertThat(output, containsString("cloud-run-ready self-test passed"))
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
}
