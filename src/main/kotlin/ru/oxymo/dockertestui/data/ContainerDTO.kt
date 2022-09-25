package ru.oxymo.dockertestui.data

data class ContainerDTO(
    val id: String,
    val image: String,
    val command: String,
    val created: Long,
    val status: String,
    val state: String,
    val ports: String,
    val names: String
) {

}