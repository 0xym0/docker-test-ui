package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Pre
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.spring.annotation.SpringComponent

@SpringComponent
class LoggingDialog : Div() {

    private final val dialog = Dialog()
    private final val content = Div()

    init {
        val scroller = Scroller(content)
        scroller.scrollDirection = Scroller.ScrollDirection.BOTH
        dialog.headerTitle = "Container log"
        dialog.add(scroller)
        dialog.footer.add(createCloseButton())

        this.add(dialog)
    }

    private fun createCloseButton(): Button {
        val button = Button("Close")
        button.addClickListener { closeDialog() }
        return button
    }

    fun closeDialog() {
        content.removeAll()
        dialog.close()
    }

    fun openDialog(logLines: String) {
        content.add(Pre(logLines))
        dialog.open()
    }

}