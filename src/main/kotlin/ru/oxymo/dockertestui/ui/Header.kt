package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.spring.annotation.SpringComponent
import org.springframework.beans.factory.annotation.Autowired

@SpringComponent
class Header @Autowired constructor(private val connectionDialog: ConnectionDialog) : HorizontalLayout() {

    private final val h1 = H1("Docker Test UI")
    private final val settingsButton = Button(Icon(VaadinIcon.COG))

    init {
        h1.style.set("font-size", "24px").set("margin", "0 0 0 0")
        settingsButton.addClickListener { connectionDialog.openDialog() }
        settingsButton.style.set("margin-left", "auto")
        this.alignItems = FlexComponent.Alignment.CENTER
        this.justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
        this.add(h1, connectionDialog, settingsButton)
    }

}