package ru.oxymo.dockertestui.util

import com.vaadin.flow.shared.Registration
import ru.oxymo.dockertestui.data.NotificationDTO
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Consumer

object NotificationPusher {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val listeners = LinkedList<Consumer<NotificationDTO>>()

    @Synchronized
    fun register(listener: Consumer<NotificationDTO>): Registration {
        listeners.add(listener)
        return Registration {
            synchronized(NotificationPusher::class.java) {
                listeners.remove(listener)
            }
        }
    }

    @Synchronized
    fun broadcast(notification: NotificationDTO) {
        listeners.forEach {
            executor.execute { it.accept(notification) }
        }
    }

}