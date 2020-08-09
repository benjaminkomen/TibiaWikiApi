package com.tibiawiki

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TibiaWikiApiApplication

fun main(args: Array<String>) {
    runApplication<TibiaWikiApiApplication>(*args)
}