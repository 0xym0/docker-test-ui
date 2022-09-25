package ru.oxymo.dockertestui.service

import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.transport.DockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient.Request
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class DockerConnector(
    @Autowired private val dockerHttpClient: DockerHttpClient,
    val dockerConfiguration: DockerClientConfig
) {

    private val log = LoggerFactory.getLogger(DockerConnector::class.java)

    @PostConstruct
    fun ping() {
        val request: Request = Request.builder()
            .method(Request.Method.GET)
            .path("/v1.40/info")
//            .path("/_ping")
            .build()

        try {
            dockerHttpClient.execute(request).use { response ->
                log.info("Response state: {}", response.statusCode)
                log.info("Response headers: {}", response.headers)
                log.info("Response body: {}", response.body.readBytes().toString(Charsets.UTF_8))
            }
        } catch (e: RuntimeException) {
            log.error("Ping for docker API failed for host...")
        }
    }

}