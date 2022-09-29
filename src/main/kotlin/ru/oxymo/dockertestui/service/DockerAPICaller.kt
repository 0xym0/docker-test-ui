package ru.oxymo.dockertestui.service

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.oxymo.dockertestui.data.ContainerDTO
import ru.oxymo.dockertestui.util.Constants.EMPTY_STRING

@Service
class DockerAPICaller @Autowired constructor(
    private val dockerConnector: DockerConnector
) {

    private val log = LoggerFactory.getLogger(DockerAPICaller::class.java)
    fun getContainers(): List<ContainerDTO> {
        if (!dockerConnector.isValidConfiguration()) {
            return emptyList()
        }
        val containerList = dockerConnector.getDockerClient()
            .listContainersCmd().withShowAll(true).exec()
        return containerList.map {
            ContainerDTO(
                it.id,
                it.image,
                it.command,
                it.created,
                it.status,
                it.state,
                it.ports.joinToString(", \n"),
                it.names.joinToString(", \n")
            )
        }
    }

    fun startContainer(containerDTO: ContainerDTO) {
        if (!dockerConnector.isValidConfiguration()) {
            log.warn("Invalid docker configuration. Unable to start container with id = ${containerDTO.id}")
            return
        }
        log.info("Starting container with id = {}", containerDTO.id)
        dockerConnector.getDockerClient().startContainerCmd(containerDTO.id).exec()
    }

    fun stopContainer(containerDTO: ContainerDTO) {
        if (!dockerConnector.isValidConfiguration()) {
            log.warn("Invalid docker configuration. Unable to stop container with id = ${containerDTO.id}")
            return
        }
        log.info("Stopping container with id = {}", containerDTO.id)
        dockerConnector.getDockerClient().stopContainerCmd(containerDTO.id).exec()
    }

    fun getContainerLogs(containerDTO: ContainerDTO): String {
        if (!dockerConnector.isValidConfiguration()) {
            log.warn(
                "Invalid docker configuration. " +
                        "Unable to obtain logs for container with id = ${containerDTO.id}"
            )
            return EMPTY_STRING
        }
        log.info("Showing logs for container with id = ${containerDTO.id}")
        val stringBuilder = StringBuilder()
        val response = dockerConnector.getDockerClient().logContainerCmd(containerDTO.id)
            .withStdErr(true)
            .withStdOut(true)
            .withFollowStream(true)
            .withTail(1000)
            .exec(object : ResultCallback.Adapter<Frame>() {
                override fun onNext(frame: Frame) {
                    val logPart = frame.payload.toString(Charsets.UTF_8)
                    log.trace("Log part for container with id = ${containerDTO.id}: $logPart")
                    stringBuilder.append(logPart)
                }
            })
        try {
            response.awaitCompletion()
            return stringBuilder.toString()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

}