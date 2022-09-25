package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.spring.annotation.SpringComponent
import org.springframework.beans.factory.annotation.Autowired

@SpringComponent
class Header @Autowired constructor(private val connectionDialog: ConnectionDialog) : VerticalLayout() {

    private val h1 = H1("Docker Test UI")
    private val settingsButton = Button(Icon(VaadinIcon.COG)) {
        connectionDialog.openDialog()
    }

    init {
        h1.style.set("font-size", "24px").set("margin", "0 0 0 0")
        val container = HorizontalLayout(h1, settingsButton)
        container.alignItems = FlexComponent.Alignment.CENTER
        container.justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
        container.setWidthFull()

        this.isPadding = false
        this.add(connectionDialog, container)
    }

}