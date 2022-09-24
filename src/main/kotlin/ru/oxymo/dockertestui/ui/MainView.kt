package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

@Route
class MainView @Autowired constructor(private val header: Header,
                                      private val containerGrid: ContainerGrid) : VerticalLayout() {

    init {
        add(
            header,
            containerGrid
        )
    }

}