package ru.oxymo.dockertestui.data

data class DockerConfigurationDTO(
    val dockerHost: String,
    val dockerCertPath: String,
    val dockerTlsVerify: Boolean,
    val dockerApiVersion: String,
)