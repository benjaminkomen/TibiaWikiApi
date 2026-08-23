package com.tibiawiki.domain.utils

@SafeVarargs
fun <E> concatenate(vararg collections: List<E>): List<E> {
    return if (collections.isEmpty()) {
        emptyList()
    } else {
        collections.toList().flatten()
    }
}
