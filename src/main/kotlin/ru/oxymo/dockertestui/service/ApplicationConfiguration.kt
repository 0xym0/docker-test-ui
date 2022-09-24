package ru.oxymo.dockertestui.service

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class ApplicationConfiguration(
    @Value("\${docker.host:tcp://localhost:2376}") private val dockerHost: String,
    @Value("\${docker.cert.path:/home/user/.docker}") private val dockerCertPath: String,
    @Value("\${docker.tls.verify:false}") private val dockerTlsVerify: Boolean,
    @Value("\${docker.api.version:1.23}") private val dockerApiVersion: String,
    @Value("\${docker.http.client.max.connections:100}") private val dockerMaxConnections: Int,
    @Value("\${docker.http.client.response.timeout:45}") private val dockerResponseTimeout: Long,
    @Value("\${docker.http.client.connection.timeout:30}") private val dockerConnectionTimeout: Long) {

    @Bean
    fun dockerClientConfiguration(): DockerClientConfig {
        return DefaultDockerClientConfig
            .createDefaultConfigBuilder()
            .withDockerHost(dockerHost)
            .withDockerCertPath(dockerCertPath)
            .withDockerTlsVerify(dockerTlsVerify)
            .withApiVersion(dockerApiVersion)
            .build()
    }

    @Bean
    fun dockerHttpClient(configuration: DockerClientConfig): DockerHttpClient {
        return ApacheDockerHttpClient.Builder()
            .dockerHost(configuration.dockerHost)
            .sslConfig(configuration.sslConfig)
            .maxConnections(dockerMaxConnections)
            .connectionTimeout(Duration.ofSeconds(dockerConnectionTimeout))
            .responseTimeout(Duration.ofSeconds(dockerResponseTimeout))
            .build()
    }

}