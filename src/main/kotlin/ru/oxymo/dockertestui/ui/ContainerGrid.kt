package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.shared.Registration
import com.vaadin.flow.spring.annotation.SpringComponent
import com.vaadin.flow.spring.annotation.UIScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import ru.oxymo.dockertestui.data.ContainerDTO
import ru.oxymo.dockertestui.data.NotificationDTO
import ru.oxymo.dockertestui.service.DockerAPICaller
import ru.oxymo.dockertestui.util.CommonUtil
import ru.oxymo.dockertestui.util.Constants.EMPTY_STRING
import ru.oxymo.dockertestui.util.ContainerListRefresher
import ru.oxymo.dockertestui.util.NotificationPusher

@SpringComponent
@UIScope
class ContainerGrid @Autowired constructor(
    private val loggingDialog: LoggingDialog,
    private val dockerAPICaller: DockerAPICaller
) : VerticalLayout() {

    private final val grid = Grid<ContainerDTO>()
    private var gridRefresherRegistration: Registration? = null
    private var notificationPusherRegistration: Registration? = null

    init {
        grid.addColumn(ContainerDTO::id).setHeader("ID").isSortable = false
        grid.addColumn(ContainerDTO::names).setHeader("Names").isSortable = false
        grid.addColumn(ContainerDTO::image).setHeader("Image").isSortable = false
        grid.addColumn(ContainerDTO::ports).setHeader("Ports").isSortable = false
        grid.addColumn(ContainerDTO::status).setHeader("Status").isSortable = false
        grid.addComponentColumn { item -> getCellLayout(item) }

        grid.setItems(dockerAPICaller.getContainers())
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)
        grid.setSelectionMode(Grid.SelectionMode.NONE)

        this.isPadding = false
        this.isMargin = false
        this.add(
            loggingDialog,
            grid
        )
    }

    @Scheduled(
        fixedDelayString = "\${docker.test.ui.grid.update.delay.ms:10000}",
        initialDelayString = "\${docker.test.ui.grid.initial.delay.ms:10000}"
    )
    fun refreshGridItems() {
        try {
            val containerDTOList = dockerAPICaller.getContainers()
            ContainerListRefresher.broadcast(containerDTOList)
        } catch (e: RuntimeException) {
            NotificationPusher.broadcast(
                NotificationDTO(
                    "Unable to load container list" +
                            CommonUtil.getErrorTextFromException(e), true
                )
            )
        }
    }

    override fun onAttach(attachEvent: AttachEvent) {
        val ui = attachEvent.ui
        gridRefresherRegistration = ContainerListRefresher.register { containerDTOList ->
            ui.access {
                grid.setItems(containerDTOList)
            }
        }
        notificationPusherRegistration = NotificationPusher.register { notificationDTO ->
            ui.access {
                NotificationCreator.showNotification(notificationDTO.text, notificationDTO.isError)
            }
        }
    }

    override fun onDetach(detachEvent: DetachEvent) {
        gridRefresherRegistration?.remove()
        gridRefresherRegistration = null
        notificationPusherRegistration?.remove()
        notificationPusherRegistration = null
    }

    private fun getCellLayout(item: ContainerDTO): HorizontalLayout {
        val playButton = Button(Icon(VaadinIcon.PLAY)) {
            startContainer(item)
        }
        val stopButton = Button(Icon(VaadinIcon.STOP)) {
            stopContainer(item)
        }
        val showLogsButton = Button("View logs") {
            showContainerLogs(item)
        }

        return when (item.state) {
            "running" -> {
                HorizontalLayout(
                    stopButton,
                    showLogsButton
                )
            }

            "removing", "restarting" -> {
                HorizontalLayout(
                    showLogsButton
                )
            }

            "created", "exited" -> {
                HorizontalLayout(
                    playButton,
                    showLogsButton
                )
            }

            else -> {
                HorizontalLayout(
                    playButton,
                    stopButton,
                    showLogsButton
                )
            }
        }
    }

    private fun startContainer(containerDTO: ContainerDTO) {
        try {
            dockerAPICaller.startContainer(containerDTO)
            refreshGridItems()
        } catch (e: RuntimeException) {
            NotificationCreator.showErrorNotification(
                "Unable to start container with id = ${containerDTO.id}. " +
                        CommonUtil.getErrorTextFromException(e)
            )
        }
    }

    private fun stopContainer(containerDTO: ContainerDTO) {
        try {
            dockerAPICaller.stopContainer(containerDTO)
            refreshGridItems()
        } catch (e: RuntimeException) {
            NotificationCreator.showErrorNotification(
                "Unable to stop container with id = ${containerDTO.id}. " +
                        CommonUtil.getErrorTextFromException(e)
            )
        }
    }

    private fun showContainerLogs(containerDTO: ContainerDTO) {
//        TODO: make logs updatable in real time for running containers
        val logs = try {
            dockerAPICaller.getContainerLogs(containerDTO)
        } catch (e: RuntimeException) {
            NotificationCreator.showErrorNotification(
                "Unable to load logs for container with id = ${containerDTO.id}. " +
                        CommonUtil.getErrorTextFromException(e)
            )
            EMPTY_STRING
        }
        loggingDialog.openDialog(logs)
    }

}