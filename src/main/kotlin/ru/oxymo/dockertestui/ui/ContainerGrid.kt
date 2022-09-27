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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import ru.oxymo.dockertestui.data.ContainerDTO
import ru.oxymo.dockertestui.service.DockerAPICaller
import ru.oxymo.dockertestui.util.ContainerListRefresher

@SpringComponent
class ContainerGrid @Autowired constructor(
    private val loggingDialog: LoggingDialog,
    private val dockerAPICaller: DockerAPICaller
) : VerticalLayout() {

    private final val grid = Grid<ContainerDTO>()
    private var registration: Registration? = null

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

    @Scheduled(fixedDelayString = "\${docker.test.ui.grid.update.delay.ms:10000}",
        initialDelayString = "\${docker.test.ui.grid.initial.delay.ms:10000}")
    fun refresh() {
        val containerDTOList = dockerAPICaller.getContainers()
        ContainerListRefresher.broadcast(containerDTOList)
    }

    override fun onAttach(attachEvent: AttachEvent) {
        val ui = attachEvent.ui
        registration = ContainerListRefresher.register { containerDTOList ->
            ui.access {
                grid.setItems(containerDTOList)
            }
        }
    }

    override fun onDetach(detachEvent: DetachEvent) {
        registration?.remove()
        registration = null
    }

    private fun getCellLayout(item: ContainerDTO): HorizontalLayout {
        val playButton = Button(Icon(VaadinIcon.PLAY)) {
            dockerAPICaller.startContainer(item)
            val containerDTOList = dockerAPICaller.getContainers()
            ContainerListRefresher.broadcast(containerDTOList)
        }
        val stopButton = Button(Icon(VaadinIcon.STOP)) {
            dockerAPICaller.stopContainer(item)
            val containerDTOList = dockerAPICaller.getContainers()
            ContainerListRefresher.broadcast(containerDTOList)
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

    private fun showContainerLogs(container: ContainerDTO) {
        val logs = dockerAPICaller.getContainerLogs(container)
        loggingDialog.openDialog(logs)
    }

}