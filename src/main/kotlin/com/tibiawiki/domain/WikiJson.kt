package com.tibiawiki.domain

/**
 * Wiki-key JSON object used on GET /api (infobox and loot parameter names).
 * Do not remap these keys to Kotlin property names without a `/api/v2` contract.
 */
typealias WikiJson = Map<String, Any>
