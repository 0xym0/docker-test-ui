package ru.oxymo.dockertestui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DockerTestUiApplication

fun main(args: Array<String>) {
    runApplication<DockerTestUiApplication>(*args)
}
