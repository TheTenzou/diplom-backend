package ru.tenzou.tsodgis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TsodgisApplication

fun main(args: Array<String>) {
	runApplication<TsodgisApplication>(*args)
}
