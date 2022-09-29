package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Pre
import com.vaadin.flow.spring.annotation.SpringComponent
import com.vaadin.flow.spring.annotation.UIScope

@SpringComponent
@UIScope
class LoggingDialog : Div() {

    private final val dialog = Dialog()
    private final val content = Div()

    init {
        dialog.headerTitle = "Container log"
        dialog.add(content)
        dialog.footer.add(Button("Close") {
            dialog.close()
        })

        this.add(dialog)
    }

    fun openDialog(logLines: String) {
        content.removeAll()
        content.add(Pre(logLines))
        dialog.open()
    }

}