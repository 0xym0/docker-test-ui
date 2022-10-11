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
import ru.oxymo.dockertestui.service.DockerAPICaller
import ru.oxymo.dockertestui.util.ContainerListRefresher
import ru.oxymo.dockertestui.util.NotificationPusher
import java.util.*

@SpringComponent
@UIScope
class ContainerGrid @Autowired constructor(
    private val dockerAPICaller: DockerAPICaller
) : VerticalLayout() {

    private val grid = Grid<ContainerDTO>()
    private val loggingDialogMap: MutableMap<String, LoggingDialog> = mutableMapOf()
    private var gridRefresherRegistration: Registration? = null
    private var notificationPusherRegistration: Registration? = null
    private val gridUID = UUID.randomUUID().toString()
    private var isFirstGridUpdate = true

    init {
        grid.addColumn(ContainerDTO::id).setHeader("ID").isSortable = false
        grid.addColumn(ContainerDTO::names).setHeader("Names").isSortable = false
        grid.addColumn(ContainerDTO::image).setHeader("Image").isSortable = false
        grid.addColumn(ContainerDTO::ports).setHeader("Ports").isSortable = false
        grid.addColumn(ContainerDTO::status).setHeader("Status").isSortable = false
        grid.addComponentColumn { item -> getCellLayout(item) }

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)
        grid.setSelectionMode(Grid.SelectionMode.NONE)

        this.isPadding = false
        this.isMargin = false
        this.setSizeFull()
        this.add(
            grid
        )
    }

    @Scheduled(
        fixedDelayString = "\${docker.test.ui.grid.update.delay.ms:10000}"
    )
    fun refreshGridItems() {
        val containerDTOList = dockerAPICaller.getContainers(!isFirstGridUpdate)
        ContainerListRefresher.broadcast(containerDTOList)
        if (isFirstGridUpdate) {
            isFirstGridUpdate = false
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
        dockerAPICaller.startContainer(containerDTO.id)
        refreshGridItems()
    }

    private fun stopContainer(containerDTO: ContainerDTO) {
        dockerAPICaller.stopContainer(containerDTO.id)
        refreshGridItems()
    }

    private fun showContainerLogs(containerDTO: ContainerDTO) {
        val containerID = containerDTO.id
        val dialog: LoggingDialog =
            if (loggingDialogMap.contains(containerID)) {
                loggingDialogMap[containerID] as LoggingDialog
            } else {
                val loggingDialog = LoggingDialog(gridUID, containerID, dockerAPICaller)
                this.add(loggingDialog)
                loggingDialogMap[containerID] = loggingDialog
                loggingDialog
            }
        dialog.open()
        dockerAPICaller.getContainerLogs(gridUID, containerID)
    }

}