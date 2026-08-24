package com.tibiawiki.domain.utils

import org.slf4j.LoggerFactory

object PropertiesUtil {
    private val LOG = LoggerFactory.getLogger(PropertiesUtil::class.java)

    fun getUsername(): String? = getProperty("username")

    fun getPassword(): String? = getProperty("password")

    private fun getProperty(propertyName: String): String? {
        return try {
            val props = java.util.Properties()
            val classloader = Thread.currentThread().contextClassLoader
            val inputStream = classloader.getResourceAsStream("credentials.properties")
            if (inputStream != null) {
                inputStream.use { props.load(it) }
                props.getProperty(propertyName)
            } else {
                LOG.warn("Could not read requested propertyName {} from file 'credentials.properties'.", propertyName)
                null
            }
        } catch (ex: Exception) {
            LOG.error(ex.toString())
            null
        }
    }
}
