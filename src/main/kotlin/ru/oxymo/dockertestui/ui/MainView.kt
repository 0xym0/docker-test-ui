package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

@Route
@CssImport("./styles/styles.css")
class MainView @Autowired constructor(
    header: Header,
    containerGrid: ContainerGrid
) : VerticalLayout() {

    init {
        add(
            header,
            containerGrid
        )
        setSizeFull()
    }

}