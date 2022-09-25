package ru.oxymo.dockertestui.service

import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.transport.DockerHttpClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.oxymo.dockertestui.data.ContainerDTO

@Service
class DockerAPICaller @Autowired constructor(
    dockerConfiguration: DockerClientConfig,
    dockerHttpClient: DockerHttpClient
) {

    private val log = LoggerFactory.getLogger(DockerAPICaller::class.java)
    private val dockerClient = DockerClientImpl.getInstance(dockerConfiguration, dockerHttpClient)

    fun getContainers(): List<ContainerDTO> {
        val containerList = dockerClient.listContainersCmd().withShowAll(true).exec()
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
//        return listOf(
//            ContainerDTO("skhfkfl", "qewkfkv", "run", "2 minutes ago", "exited", "18080", ""),
//            ContainerDTO("djsvdjf", "safsfsf", "run", "2 minutes ago", "exited", "18081", "")
//        )
    }

    fun ping() {
        dockerClient.pingCmd().exec()
//        TODO implement API call
    }

    fun startContainer(containerDTO: ContainerDTO) {
        log.info("Starting container with id = {}", containerDTO.id)
        dockerClient.startContainerCmd(containerDTO.id).exec()
//        TODO implement API call
    }

    fun stopContainer(containerDTO: ContainerDTO) {
        log.info("Stopping container with id = {}", containerDTO.id)
        dockerClient.stopContainerCmd(containerDTO.id).exec()
//        TODO implement API call
    }

    fun getContainerLogs(containerDTO: ContainerDTO): String {
        log.info("Showing logs for container with id = {}", containerDTO.id)
//        val text: String = dockerClient.logContainerCmd(containerDTO.id).exec()
//        TODO implement API call
        return "2022-09-24 18:28:04.738  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereHandler com.vaadin.flow.server.communication.PushAtmosphereHandler mapped to context-path: /*\n" +
                "2022-09-24 18:28:04.738  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed the following AtmosphereInterceptor mapped to AtmosphereHandler com.vaadin.flow.server.communication.PushAtmosphereHandler\n" +
                "2022-09-24 18:28:04.793  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Atmosphere is using org.atmosphere.util.VoidAnnotationProcessor for processing annotation\n" +
                "2022-09-24 18:28:04.800  INFO 10384 --- [           main] org.atmosphere.util.ForkJoinPool         : Using ForkJoinPool  java.util.concurrent.ForkJoinPool. Set the org.atmosphere.cpr.broadcaster.maxAsyncWriteThreads to -1 to fully use its power.\n" +
                "2022-09-24 18:28:04.805  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed WebSocketProtocol org.atmosphere.websocket.protocol.SimpleHttpProtocol \n" +
                "2022-09-24 18:28:04.812  INFO 10384 --- [           main] o.a.container.JSR356AsyncSupport         : JSR 356 Mapping path \n" +
                "2022-09-24 18:28:04.826  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installing Default AtmosphereInterceptors\n" +
                "2022-09-24 18:28:04.827  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.CorsInterceptor : CORS Interceptor Support\n" +
                "2022-09-24 18:28:04.827  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.CacheHeadersInterceptor : Default Response's Headers Interceptor\n" +
                "2022-09-24 18:28:04.827  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.PaddingAtmosphereInterceptor : Browser Padding Interceptor Support\n" +
                "2022-09-24 18:28:04.828  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.AndroidAtmosphereInterceptor : Android Interceptor Support\n" +
                "2022-09-24 18:28:04.828  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Dropping Interceptor org.atmosphere.interceptor.HeartbeatInterceptor\n" +
                "2022-09-24 18:28:04.828  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.SSEAtmosphereInterceptor : SSE Interceptor Support\n" +
                "2022-09-24 18:28:04.828  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.JSONPAtmosphereInterceptor : JSONP Interceptor Support\n" +
                "2022-09-24 18:28:04.830  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.JavaScriptProtocol : Atmosphere JavaScript Protocol\n" +
                "2022-09-24 18:28:04.830  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.WebSocketMessageSuspendInterceptor : org.atmosphere.interceptor.WebSocketMessageSuspendInterceptor\n" +
                "2022-09-24 18:28:04.830  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.OnDisconnectInterceptor : Browser disconnection detection\n" +
                "2022-09-24 18:28:04.830  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : \torg.atmosphere.interceptor.IdleResourceInterceptor : org.atmosphere.interceptor.IdleResourceInterceptor\n" +
                "2022-09-24 18:28:04.831  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Set org.atmosphere.cpr.AtmosphereInterceptor.disableDefaults to disable them.\n" +
                "2022-09-24 18:28:04.831  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor CORS Interceptor Support with priority FIRST_BEFORE_DEFAULT \n" +
                "2022-09-24 18:28:04.836  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor Default Response's Headers Interceptor with priority AFTER_DEFAULT \n" +
                "2022-09-24 18:28:04.837  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor Browser Padding Interceptor Support with priority AFTER_DEFAULT \n" +
                "2022-09-24 18:28:04.837  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor Android Interceptor Support with priority AFTER_DEFAULT \n" +
                "2022-09-24 18:28:04.837  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor SSE Interceptor Support with priority AFTER_DEFAULT \n" +
                "2022-09-24 18:28:04.837  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor JSONP Interceptor Support with priority AFTER_DEFAULT \n" +
                "2022-09-24 18:28:04.838  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor Atmosphere JavaScript Protocol with priority AFTER_DEFAULT \n" +
                "2022-09-24 18:28:04.838  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor org.atmosphere.interceptor.WebSocketMessageSuspendInterceptor with priority AFTER_DEFAULT \n" +
                "2022-09-24 18:28:04.838  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor Browser disconnection detection with priority AFTER_DEFAULT \n" +
                "2022-09-24 18:28:04.838  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor org.atmosphere.interceptor.IdleResourceInterceptor with priority BEFORE_DEFAULT \n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Using EndpointMapper class org.atmosphere.util.DefaultEndpointMapper\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Using BroadcasterCache: org.atmosphere.cache.UUIDBroadcasterCache\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Default Broadcaster Class: org.atmosphere.cpr.DefaultBroadcaster\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Broadcaster Shared List Resources: false\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Broadcaster Polling Wait Time 100\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Shared ExecutorService supported: true\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Messaging ExecutorService Pool Size unavailable - Not instance of ThreadPoolExecutor\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Async I/O Thread Pool Size: 200\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Using BroadcasterFactory: org.atmosphere.cpr.DefaultBroadcasterFactory\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Using AtmosphereResurceFactory: org.atmosphere.cpr.DefaultAtmosphereResourceFactory\n" +
                "2022-09-24 18:28:04.839  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Using WebSocketProcessor: org.atmosphere.websocket.DefaultWebSocketProcessor\n" +
                "2022-09-24 18:28:04.841  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Invoke AtmosphereInterceptor on WebSocket message true\n" +
                "2022-09-24 18:28:04.842  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : HttpSession supported: true\n" +
                "2022-09-24 18:28:04.842  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Atmosphere is using DefaultAtmosphereObjectFactory for dependency injection and object creation\n" +
                "2022-09-24 18:28:04.842  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Atmosphere is using async support: org.atmosphere.container.JSR356AsyncSupport running under container: Apache Tomcat/9.0.65 using javax.servlet/3.0 and jsr356/WebSocket API\n" +
                "2022-09-24 18:28:04.842  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Atmosphere Framework 2.7.3.slf4jvaadin4 started.\n" +
                "2022-09-24 18:28:04.847  INFO 10384 --- [           main] org.atmosphere.cpr.AtmosphereFramework   : Installed AtmosphereInterceptor  Track Message Size Interceptor using | with priority BEFORE_DEFAULT \n" +
                "2022-09-24 18:28:04.899  INFO 10384 --- [           main] c.v.f.s.DefaultDeploymentConfiguration   : \n" +
                "Vaadin is running in DEVELOPMENT mode - do not use for production deployments.\n" +
                "2022-09-24 18:28:04.958  INFO 10384 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''\n" +
                "2022-09-24 18:28:04.976  INFO 10384 --- [           main] r.o.d.DockerTestUiApplicationKt          : Started DockerTestUiApplicationKt in 11.994 seconds (JVM running for 13.972)\n" +
                "2022-09-24 18:28:04.983  INFO 10384 --- [onPool-worker-3] c.v.b.devserver.AbstractDevServerRunner  : Starting Vite\n" +
                "\n" +
                "------------------ Starting Frontend compilation. ------------------\n" +
                "2022-09-24 18:28:14.160  INFO 10384 --- [onPool-worker-3] c.v.b.devserver.AbstractDevServerRunner  : Running Vite to compile frontend resources. This may take a moment, please stand by...\n" +
                "2022-09-24 18:28:16.602  INFO 10384 --- [v-server-output] c.v.b.devserver.DevServerOutputTracker   : \n" +
                "2022-09-24 18:28:16.602  INFO 10384 --- [v-server-output] c.v.b.devserver.DevServerOutputTracker   :   VITE v3.0.9  ready in 2343 ms\n" +
                "\n" +
                "----------------- Frontend compiled successfully. -----------------\n" +
                "\n" +
                "2022-09-24 18:28:16.603  INFO 10384 --- [onPool-worker-3] c.v.b.devserver.AbstractDevServerRunner  : Started Vite. Time: 11621ms\n" +
                "2022-09-24 18:28:16.603  INFO 10384 --- [v-server-output] c.v.b.devserver.DevServerOutputTracker   : \n" +
                "2022-09-24 18:28:16.603  INFO 10384 --- [v-server-output] c.v.b.devserver.DevServerOutputTracker   :   ➜  Local:   http://127.0.0.1:51136/VAADIN/\n" +
                "2022-09-24 18:28:20.533  INFO 10384 --- [v-server-output] c.v.b.devserver.DevServerOutputTracker   : \n" +
                "2022-09-24 18:28:20.533  INFO 10384 --- [v-server-output] c.v.b.devserver.DevServerOutputTracker   : [TypeScript] Found 0 errors. Watching for file changes."
    }

}