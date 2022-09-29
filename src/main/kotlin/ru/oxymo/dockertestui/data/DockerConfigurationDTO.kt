package ru.oxymo.dockertestui.data

import ru.oxymo.dockertestui.util.Constants.EMPTY_STRING
import javax.validation.constraints.NotEmpty

data class DockerConfigurationDTO(
    @NotEmpty
    var dockerHost: String = EMPTY_STRING,
    var dockerCertPath: String = EMPTY_STRING,
    var dockerTlsVerify: Boolean = false,
    @NotEmpty
    var dockerApiVersion: String = EMPTY_STRING
)