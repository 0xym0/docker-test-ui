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
        grid.addColumn(ContainerDTO::id).setHeader("ID")
        grid.addColumn(ContainerDTO::image).setHeader("Image")
        grid.addColumn(ContainerDTO::ports).setHeader("Ports")
        grid.addColumn(ContainerDTO::status).setHeader("Status")
        grid.addComponentColumn { item -> getCellLayout(item) }
//        grid.addComponentColumn { item ->
//            Button(Icon(VaadinIcon.PLAY)) {
//                dockerAPICaller.startContainer(item)
//            }
//        }
//        grid.addComponentColumn { item ->
//            Button(Icon(VaadinIcon.STOP)) {
//                dockerAPICaller.stopContainer(item)
//            }
//        }
//        grid.addComponentColumn { item ->
//            Button("Show logs") {
//                showContainerLogs(item)
//            }
//        }

        grid.setItems(dockerAPICaller.getContainers())
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)
//        grid.isDetailsVisibleOnClick = false
        grid.setSelectionMode(Grid.SelectionMode.NONE)

        this.isPadding = false
        this.isMargin = false
        this.add(
            loggingDialog,
            grid
        )
    }

    private fun getCellLayout(item: ContainerDTO): HorizontalLayout {
        val playIcon = Icon(VaadinIcon.PLAY)
        val stopIcon = Icon(VaadinIcon.STOP)
        val playButton = Button(playIcon) {
            dockerAPICaller.startContainer(item)
        }
        val stopButton = Button(stopIcon) {
            dockerAPICaller.stopContainer(item)
        }
        val showLogsButton = Button("Show logs") {
            showContainerLogs(item)
        }
        return when (item.state) {
            "running" -> {
                playIcon.color = "gray"
                stopIcon.color = "red"
                HorizontalLayout(
                    playIcon,
                    stopButton,
                    showLogsButton
                )
            }

            "restarting" -> {
                playIcon.color = "gray"
                stopIcon.color = "gray"
                HorizontalLayout(
                    playIcon,
                    stopIcon,
                    showLogsButton
                )
            }

            "removing", "exited" -> {
                playIcon.color = "green"
                stopIcon.color = "gray"
                HorizontalLayout(
                    playButton,
                    stopIcon,
                    showLogsButton
                )
            }

            else -> {
                playIcon.color = "green"
                stopIcon.color = "red"
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