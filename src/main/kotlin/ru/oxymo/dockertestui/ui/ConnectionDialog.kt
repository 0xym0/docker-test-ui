package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.spring.annotation.SpringComponent
import com.vaadin.flow.spring.annotation.UIScope
import org.springframework.beans.factory.annotation.Autowired
import ru.oxymo.dockertestui.service.DockerConnector

@SpringComponent
@UIScope
class ConnectionDialog @Autowired constructor(
    private val dockerConnector: DockerConnector
) : Div() {

    private final val dialog = Dialog()
    private final val dockerHostField = TextField("Docker host")
    private final val dockerAPIVersionField = TextField("Docker API version")

    init {
        dialog.headerTitle = "Docker settings"
        dialog.add(createDialogLayout())
        dialog.footer.add(createCancelButton())
        dialog.footer.add(createSaveButton())

        this.add(dialog)
    }

    private fun createDialogLayout(): FormLayout {
        val layout = FormLayout(
            dockerHostField,
            dockerAPIVersionField,
        )
        layout.addClassName("docker-test-ui-configuration-form-layout")
        return layout
    }

    private fun createCancelButton(): Button {
        return Button("Cancel") { dialog.close() }
    }

    private fun createSaveButton(): Button {
        val button = Button("Save")
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        button.addClickListener {
//            dockerConnector.updateDockerConfiguration()
            dialog.close()
        }
        button.addClickShortcut(Key.ENTER)
        return button
    }

    fun openDialog() {
        val configurationDTO = dockerConnector.getDockerConfigurationDTO()
        dockerHostField.value = configurationDTO.dockerHost
        dockerAPIVersionField.value = configurationDTO.dockerApiVersion
        dialog.open()
    }

}