package com.tibiawiki.process

import com.tibiawiki.domain.objects.WikiObject

sealed class ModifyResult {
    data class Success(val wikiObject: WikiObject) : ModifyResult()
    data class Failure(val cause: Throwable) : ModifyResult()
}
