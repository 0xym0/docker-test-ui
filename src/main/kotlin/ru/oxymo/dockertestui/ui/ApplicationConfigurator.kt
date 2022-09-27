package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.shared.communication.PushMode
import com.vaadin.flow.shared.ui.Transport

@PageTitle("Docker Test UI")
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
class ApplicationConfigurator : AppShellConfigurator