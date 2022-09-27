package ru.oxymo.dockertestui.util

import com.vaadin.flow.shared.Registration
import ru.oxymo.dockertestui.data.ContainerDTO
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Consumer

object ContainerListRefresher {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val listeners = LinkedList<Consumer<List<ContainerDTO>>>()

    @Synchronized
    fun register(listener: Consumer<List<ContainerDTO>>): Registration {
        listeners.add(listener)
        return Registration {
            synchronized(ContainerListRefresher::class.java) {
                listeners.remove(listener)
            }
        }
    }

    @Synchronized
    fun broadcast(data: List<ContainerDTO>) {
        listeners.forEach {
            executor.execute { it.accept(data) }
        }
    }

}