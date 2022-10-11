package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.shared.Registration
import ru.oxymo.dockertestui.service.DockerAPICaller
import ru.oxymo.dockertestui.util.CommonUtil
import ru.oxymo.dockertestui.util.LogDataUpdater

class LoggingDialog(
    private val guid: String,
    private val containerID: String,
    private val dockerAPICaller: DockerAPICaller
) : Dialog() {

    private val content = Div()
    private val loggingKey = CommonUtil.getLoggingKey(guid, containerID)
    private var logDataUpdaterRegistration: Registration? = null

    init {
        headerTitle = "Container log"
        content.addClassName("logging-dialog-body")
        add(content)
        footer.add(Button("Close") {
            close()
        })
    }

    override fun onAttach(attachEvent: AttachEvent) {
        val ui = attachEvent.ui
        logDataUpdaterRegistration = LogDataUpdater.register(loggingKey) { logText ->
            ui.access {
                content.add(Text(logText))
            }
        }
    }

    override fun onDetach(detachEvent: DetachEvent) {
        logDataUpdaterRegistration?.remove()
        logDataUpdaterRegistration = null
    }

    override fun open() {
        content.removeAll()
        super.open()
    }

    override fun close() {
        dockerAPICaller.resetContainerLogsFollowing(guid, containerID)
        super.close()
    }

}