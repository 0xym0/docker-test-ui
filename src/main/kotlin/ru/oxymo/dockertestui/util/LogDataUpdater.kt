package ru.oxymo.dockertestui.util

import com.vaadin.flow.shared.Registration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Consumer

object LogDataUpdater {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val listenersMap = ConcurrentHashMap<String, Consumer<String>>()

    @Synchronized
    fun register(listenerID: String, listener: Consumer<String>): Registration {
        listenersMap[listenerID] = listener
        return Registration {
            synchronized(LogDataUpdater::class.java) {
                listenersMap.remove(listenerID)
            }
        }
    }

    @Synchronized
    fun broadcast(listenerID: String, logText: String) {
        executor.execute {
            listenersMap[listenerID]?.accept(logText)
        }
    }

}