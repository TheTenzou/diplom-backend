package ru.tenzou.tsodgis.entity

/**
 * status define whether row considered to be deleted or not
 */
enum class Status {
    /**
     * row isn't deleted
     */
    ACTIVE,

    /**
     * row deleted
     */
    DELETED
}
