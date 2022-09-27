package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.spring.annotation.SpringComponent
import org.springframework.beans.factory.annotation.Autowired
import ru.oxymo.dockertestui.data.ContainerDTO
import ru.oxymo.dockertestui.service.DockerAPICaller

@SpringComponent
class ContainerGrid @Autowired constructor(
    private val loggingDialog: LoggingDialog,
    private val dockerAPICaller: DockerAPICaller
) : VerticalLayout() {

    private final val grid = Grid<ContainerDTO>()

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

    private fun getCellLayout(item: ContainerDTO): HorizontalLayout {
        val playButton = Button(Icon(VaadinIcon.PLAY)) {
            dockerAPICaller.startContainer(item)
        }
        val stopButton = Button(Icon(VaadinIcon.STOP)) {
            dockerAPICaller.stopContainer(item)
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