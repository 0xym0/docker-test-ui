package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.NativeButtonRenderer
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
        grid.addColumn(ContainerDTO::created).setHeader("Created")
        grid.addComponentColumn {item ->
            Button(Icon(VaadinIcon.PLAY)) {
                dockerAPICaller.startContainer(item)
            }
        }
        grid.addComponentColumn {item ->
            Button(Icon(VaadinIcon.STOP)) {
                dockerAPICaller.stopContainer(item)
            }
        }
        grid.addColumn(NativeButtonRenderer("Show logs") {
                containerDTO -> showContainerLogs(containerDTO) })

        grid.setItems(dockerAPICaller.getContainers())
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)

        this.add(
            loggingDialog,
            grid
        )
    }

    private fun showContainerLogs(container: ContainerDTO) {
        val logs = dockerAPICaller.getContainerLogs(container)
        loggingDialog.openDialog(logs.split("\n"))
    }

}