package ru.oxymo.dockertestui.service

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.oxymo.dockertestui.data.DockerConfigurationDTO
import java.time.Duration

@Service
class DockerConnector(
    @Value("\${docker.host:tcp://localhost:2376}") dockerHost: String,
    @Value("\${docker.cert.path:/home/user/.docker}") dockerCertPath: String,
    @Value("\${docker.tls.verify:false}") dockerTlsVerify: Boolean,
    @Value("\${docker.api.version:1.40}") dockerApiVersion: String,
    @Value("\${docker.http.client.max.connections:100}") private val dockerMaxConnections: Int,
    @Value("\${docker.http.client.response.timeout:45}") private val dockerResponseTimeout: Long,
    @Value("\${docker.http.client.connection.timeout:30}") private val dockerConnectionTimeout: Long,
) {

    private val log = LoggerFactory.getLogger(DockerConnector::class.java)
    private var configurationDTO = DockerConfigurationDTO(
        dockerHost, dockerCertPath, dockerTlsVerify, dockerApiVersion
    )
    private lateinit var dockerClient: DockerClient
    private var isValidDockerConfiguration = false

    init {
        val dockerConfiguration = buildDockerClientConfig(configurationDTO)
        val dockerHttpClient = buildDockerHttpClient(dockerConfiguration)
        dockerClient = DockerClientImpl.getInstance(dockerConfiguration, dockerHttpClient)
        isValidDockerConfiguration = isDockerClientConfigurationValid(configurationDTO, dockerClient)
    }

    private fun buildDockerClientConfig(configuration: DockerConfigurationDTO): DockerClientConfig =
        DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(configuration.dockerHost)
            .withDockerCertPath(configuration.dockerCertPath)
            .withDockerTlsVerify(configuration.dockerTlsVerify)
            .withApiVersion(configuration.dockerApiVersion)
            .build()

    private fun buildDockerHttpClient(dockerConfiguration: DockerClientConfig): DockerHttpClient =
        ApacheDockerHttpClient.Builder()
            .dockerHost(dockerConfiguration.dockerHost)
            .sslConfig(dockerConfiguration.sslConfig)
            .maxConnections(dockerMaxConnections)
            .connectionTimeout(Duration.ofSeconds(dockerConnectionTimeout))
            .responseTimeout(Duration.ofSeconds(dockerResponseTimeout))
            .build()

    private fun isDockerClientConfigurationValid(
        configuration: DockerConfigurationDTO,
        dockerClient: DockerClient
    ) = try {
        dockerClient.pingCmd().exec()
        true
    } catch (e: RuntimeException) {
        log.error(
            "Ping command failed for ${configuration.dockerHost}/${configuration.dockerApiVersion} " +
                    if (configuration.dockerTlsVerify) "with TLS" else "without TLS" +
                            ". Setting docker configuration as invalid", e
        )
        false
    }

    fun getDockerClient() = dockerClient

    fun getDockerConfigurationDTO() = configurationDTO

    fun isValidConfiguration() = isValidDockerConfiguration

    fun updateDockerConfiguration(configuration: DockerConfigurationDTO): Boolean {
        val dockerConfiguration = buildDockerClientConfig(configuration)
        val dockerHttpClient = buildDockerHttpClient(dockerConfiguration)
        val dockerClient = DockerClientImpl.getInstance(dockerConfiguration, dockerHttpClient)
        val isValidDockerConfiguration = isDockerClientConfigurationValid(configuration, dockerClient)
        if (isValidDockerConfiguration) {
            log.info("Configuration check is successful. Saving new parameters: $configuration")
            this.configurationDTO = configuration
            this.dockerClient = dockerClient
            this.isValidDockerConfiguration = true
        } else {
            log.warn("Invalid new configuration: $configuration. " +
                    "Using previous parameters for connecting docker: $configurationDTO")
        }
        return isValidDockerConfiguration
    }

}