package com.tibiawiki.domain.wiki

/**
 * Rejected a bulk `?expand=true` (or equivalent) fetch that would stampede Fandom.
 * Mapped to HTTP 413.
 */
class ExpandTooLargeException(
    val requested: Int,
    val max: Int
) : RuntimeException("expand requested $requested pages; max is $max")
