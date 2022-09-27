package ru.oxymo.dockertestui.ui

import com.github.dockerjava.core.DockerClientConfig
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.spring.annotation.SpringComponent
import org.springframework.beans.factory.annotation.Autowired

@SpringComponent
class ConnectionDialog @Autowired constructor(
    private val dockerClientConfiguration: DockerClientConfig
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
        dockerHostField.value = dockerClientConfiguration.dockerHost.toString()
        dockerAPIVersionField.value = dockerClientConfiguration.apiVersion.version
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
//            dockerClientConfiguration.dockerHost
            dialog.close()
        }
        button.addClickShortcut(Key.ENTER)
        return button
    }

    fun openDialog() {
        dockerHostField.value = dockerClientConfiguration.dockerHost.toString()
        dockerAPIVersionField.value = dockerClientConfiguration.apiVersion.version
        dialog.open()
    }

}