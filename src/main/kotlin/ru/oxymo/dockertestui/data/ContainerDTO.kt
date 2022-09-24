package ru.oxymo.dockertestui.data

data class ContainerDTO(
    val id: String,
    val image: String,
    val command: String,
    val created: String,
    val status: String,
    val ports: String,
    val names: String
) {

}