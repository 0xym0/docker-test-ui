package ru.oxymo.dockertestui.ui

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

object NotificationCreator {

    fun showNotification(text: String, isError: Boolean) {
        val notification = Notification()
        val textElement = Div(Text(text))
        val layout =
            if (isError) {
                notification.position = Notification.Position.BOTTOM_STRETCH
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR)

                val closeButton = Button(Icon("lumo", "cross")) {
                    notification.close()
                }
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                closeButton.element.setAttribute("aria-label", "Close")

                HorizontalLayout(textElement, closeButton)
            } else {
                notification.position = Notification.Position.BOTTOM_END
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
                notification.duration = 5000

                HorizontalLayout(textElement)
            }

        notification.add(layout)
        notification.open()
    }

    fun showErrorNotification(text: String) {
        showNotification(text, true)
    }

    fun showSuccessNotification(text: String) {
        showNotification(text, false)
    }

}