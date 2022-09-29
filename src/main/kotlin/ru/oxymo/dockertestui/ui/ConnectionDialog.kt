package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.ValidationException
import com.vaadin.flow.spring.annotation.SpringComponent
import com.vaadin.flow.spring.annotation.UIScope
import org.springframework.beans.factory.annotation.Autowired
import ru.oxymo.dockertestui.data.DockerConfigurationDTO
import ru.oxymo.dockertestui.service.DockerConnector
import ru.oxymo.dockertestui.util.CommonUtil

@SpringComponent
@UIScope
class ConnectionDialog @Autowired constructor(
    private val dockerConnector: DockerConnector
) : Div() {

    private final val dialog = Dialog()
    private final val dockerHostField = TextField("Docker host")
    private final val dockerAPIVersionField = TextField("Docker API version")
    private final val dockerCertificatesPathField = TextField("Docker Cert local path")
    private final val dockerTlsVerifyCheckbox = Checkbox("Is TLS enabled") {
        dockerCertificatesPathField.isVisible = it.value
    }
    private final val formBinder = BeanValidationBinder(DockerConfigurationDTO::class.java)

    init {
        formBinder.bind(dockerHostField, "dockerHost")
        formBinder.bind(dockerAPIVersionField, "dockerApiVersion")
        formBinder.bind(dockerTlsVerifyCheckbox, "dockerTlsVerify")
        formBinder.bind(dockerCertificatesPathField, "dockerCertPath")

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
            dockerTlsVerifyCheckbox,
            dockerCertificatesPathField
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
            formBinder.validate()
            val configurationDTO = DockerConfigurationDTO()
            try {
                formBinder.writeBean(configurationDTO)
            } catch (e: ValidationException) {
                NotificationCreator.showErrorNotification(CommonUtil.getErrorTextFromException(e))
                return@addClickListener
            }
            if (dockerConnector.updateDockerConfiguration(configurationDTO)) {
                NotificationCreator.showSuccessNotification("New configuration saved successfully")
            } else {
                NotificationCreator.showErrorNotification("Error on applying new configuration")
            }
            dialog.close()
        }
        button.addClickShortcut(Key.ENTER)
        return button
    }

    fun openDialog() {
        val configurationDTO = dockerConnector.getDockerConfigurationDTO()
        formBinder.readBean(configurationDTO)
        dialog.open()
    }

}