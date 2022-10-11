package ru.oxymo.dockertestui.service

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.oxymo.dockertestui.data.ContainerDTO
import ru.oxymo.dockertestui.data.NotificationDTO
import ru.oxymo.dockertestui.util.CommonUtil
import ru.oxymo.dockertestui.util.LogDataUpdater
import ru.oxymo.dockertestui.util.NotificationPusher
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Service
class DockerAPICaller @Autowired constructor(
    private val dockerConnector: DockerConnector
) {

    private val loggingEnabledMap = ConcurrentHashMap<String, Boolean>()
    private val log = LoggerFactory.getLogger(DockerAPICaller::class.java)
    private val executor: Executor = Executors.newCachedThreadPool()

    fun getContainers(checkConfiguration: Boolean = true): List<ContainerDTO> {
        if (checkConfiguration && !dockerConnector.isValidConfiguration()) {
            log.warn("Invalid docker configuration. Unable to list available containers")
            return emptyList()
        }
        val containerList =
            try {
                dockerConnector.getDockerClient()
                    .listContainersCmd()
                    .withShowAll(true)
                    .exec()
            } catch (e: RuntimeException) {
                val errorMessage = "Unable to load container list. "
                log.error(errorMessage, e)
                NotificationPusher.broadcast(
                    NotificationDTO(
                        errorMessage + CommonUtil.getErrorTextFromException(e), true
                    )
                )
                emptyList()
            }
        return containerList
            .filterNotNull()
            .map {
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

    fun startContainer(containerID: String) {
        log.info("Starting container with id = {}", containerID)
        executor.execute {
            try {
                dockerConnector.getDockerClient().startContainerCmd(containerID).exec()
            } catch (e: RuntimeException) {
                val errorMessage = "Unable to start container with id = $containerID. "
                log.error(errorMessage, e)
                NotificationPusher.broadcast(
                    NotificationDTO(
                        errorMessage + CommonUtil.getErrorTextFromException(e), true
                    )
                )
            }
        }
    }

    fun stopContainer(containerID: String) {
        log.info("Stopping container with id = {}", containerID)
        executor.execute {
            try {
                dockerConnector.getDockerClient().stopContainerCmd(containerID).exec()
            } catch (e: RuntimeException) {
                val errorMessage = "Unable to stop container with id = $containerID. "
                log.error(errorMessage, e)
                NotificationPusher.broadcast(
                    NotificationDTO(
                        errorMessage + CommonUtil.getErrorTextFromException(e), true
                    )
                )
            }
        }
    }

    fun getContainerLogs(guid: String, containerID: String) {
        val loggingKey = CommonUtil.getLoggingKey(guid, containerID)
        loggingEnabledMap[loggingKey] = true

        log.info("Showing logs for container with id = $containerID and UI with GUID = $guid")
        val logContainerCommand = dockerConnector.getDockerClient()
            .logContainerCmd(containerID)
            .withStdErr(true)
            .withStdOut(true)
            .withFollowStream(true)

        val resultCallback = object : ResultCallback.Adapter<Frame>() {
            override fun onNext(frame: Frame) {
                val logPart = frame.payload.toString(Charsets.UTF_8)
                log.trace("Log part for loggingKey = $loggingKey: $logPart")
                LogDataUpdater.broadcast(loggingKey, logPart)
                if (loggingEnabledMap[loggingKey] == false) {
                    this.close()
                }
            }
        }

        executor.execute {
            try {
                val response = logContainerCommand.exec(resultCallback)
                response.awaitCompletion()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            } catch (e: RuntimeException) {
                val errorMessage = "Unable to load container logs for loggingKey = $loggingKey. "
                log.error(errorMessage, e)
                NotificationPusher.broadcast(
                    NotificationDTO(
                        errorMessage + CommonUtil.getErrorTextFromException(e), true
                    )
                )
            }
        }
    }

    fun resetContainerLogsFollowing(guid: String, containerID: String) {
        val loggingKey = CommonUtil.getLoggingKey(guid, containerID)
        loggingEnabledMap[loggingKey] = false
    }

}